package code
package snippet


import net.liftweb._
import common._
import http._
import lib._
import model._
import util._
import sitemap._
import js.JsCmds._
import scala.xml._
import util.Helpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.{JE,JsCmd,JsCmds}


class LostPassword extends Loggable with UserHelper with ReCaptcha {


  def render = {

    var userid = ""
    var count = 0
    val user = User.createRecord

    def process() : JsCmd = {

      user.email.validate ::: validateCaptcha() match{

        case Nil => {
          //logger.info("\n Nil ~~> "+ Nil +"!\n")//depurar
          User.findUser( userid ) match{
            case Full(user) if user.verified.get == true =>
              User.createForUserId(user)//reset field User.uniqueId
              User.sendLoginToken(user)//send email with user key
              ChangePassword.createRecord.userId( user.id.get ).timerequest( millis ).save() //create document
              S.notice("sendEmail", <div> <span class="glyphicon glyphicon-warning-sign"></span> <span data-lift="Loc.i">lost-password.email.send</span></div>)
              S.redirectTo("/")
            case _ =>
              S.error("lostPasswordError", <div> <span class="glyphicon glyphicon-warning-sign"></span> <span data-lift="Loc.i">lost-password.error</span></div>)//beta
          }
        }

        case errors : List[FieldError] => {
          S.error(errors) //beta
          logger.info("\n\n errors ~~> "+ errors +"!\n\n")//depurar
          count += 1
          if( count > 1 ){
            JE.JsRaw( "errorShake();" ).cmd
          }

          
          //S.error("lostPasswordError", <div> <span class="glyphicon glyphicon-warning-sign"></span> <span data-lift="Loc.i">lost-password.error</span></div>)//beta
          //S.error("reCaptcha", <div class="alert alert-danger" role="alert"> <span data-lift="Loc.i">reCaptcha.incorrect-captcha-sol</span></div>)//beta
          //S.error("emailError", <div class="alert alert-danger" role="alert"> <span data-lift="Loc.i">lost-password.email.error</span> </div>)
          JE.JsRaw( "Recaptcha.switch_type('image');" ).cmd//refresh recaptcha
        }

      }

    }

    def check_user(in:String, field: String, errorFieldId: String) = {

      val userid = in.trim

      User.findUser( userid ) match{
        case Full(user) =>
          <p class="success" id={errorFieldId}> <span class="glyphicon glyphicon-ok"></span> </p>
        case _ =>
          <p id={errorFieldId}> <span class="glyphicon glyphicon-warning-sign"></span> <span data-lift="Loc.i">lost-password.user.not-found</span></p>
      }

    }

    "@email [placeholder]"    #> ( S.?("lost-password.email.placeholder") )andThen
    "#email"                  #> ajaxLiveText( "", userid => Replace( "email_error", check_user( userid,"Email: ", "email_error" ) ),"type" -> "text" ) andThen
    "#email"                  #> SHtml.text( userid, userid = _, "maxlength" -> "40") andThen
    "#recaptcha"              #> captchaXhtml() andThen
    "@recaptcha_response_field [placeholder]"   #> ( S.?("recaptcha.instructions") )andThen
    "button *+"               #> SHtml.hidden(process)

  }


}

