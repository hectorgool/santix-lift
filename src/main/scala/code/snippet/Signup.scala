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
import http.SHtml._
import http.js.{JE,JsCmd,JsCmds}
import net.liftmodules.mongoauth._
import net.liftmodules.mongoauth.field.{PasswordField => MongoAuthPasswordField}//Renaming Members on Import


class Signup extends Loggable with UserHelper with ReCaptcha {


  def render = {

    var email = ""
    var username = ""
    var passwd = ""
    var count = 0

    def process() : JsCmd = {

      val hash = MongoAuthPasswordField.hashpw(passwd)
      val user : User = User.createRecord
        .username(username)
        .email(email)
        .password(hash)
        .timecreated(millis)
        .verified(true)
        .permissions(
          List(
            Permission("printer", "print"), 
            Permission("user", "edit", "123")
          )
        )
        .roles(List("staff"))

      assert(User.hasPermission(Permission("printer", "manage")) == false)

      user.username.validate ::: user.email.validate ::: user.password.validate ::: validateCaptcha() match{

        case Nil =>
          val newuser = user.save() //save new user in database
          User.logUserIn(newuser)
          S.notice(S.?("welcome.message", username))
          logger.info("\n Nil ~~> !\n")//depurar
          S.redirectTo("/")

        case errors : List[FieldError] =>
          S.error(errors)
          logger.info("\n errors ~~> "+ errors +"!\n")//depurar
          count += 1
          if( count > 1 ){
            JE.JsRaw( "errorShake();" ).cmd
            logger.info("\n count ~~> "+ count +"!\n")//depurar
          }
          S.error( "signupError", <div class="alert alert-danger" role="alert"> <span data-lift="Loc.i">inputs.errors</span> </div>)
          //S.error( "usernameError", <div class="alert alert-danger" role="alert"> <lift:Msg id="username" /> </div>)
          //S.error( "emailError", <div class="alert alert-danger" role="alert"> <lift:Msg id="email" /></div>)
          //S.error( "passwordError", <div class="alert alert-danger" role="alert"> <lift:Msg id="password" /></div>)
          S.error( "reCaptcha", <div class="alert alert-danger" role="alert"> <span data-lift="Loc.i">reCaptcha.incorrect-captcha-sol</span></div> )//beta        
          JE.JsRaw( "Recaptcha.switch_type('image');" ).cmd

      }

    }

    def check_username(in:String, field: String, errorFieldId: String) = {

      val username = in.trim
      val user : User = User.createRecord.username(username)

      user.username.validate match{
        case Nil =>
          <div id={errorFieldId}> <span class="glyphicon glyphicon-ok"></span> </div>
        case errors : List[FieldError] =>
          S.error(errors)
          <div class="alert alert-danger" role="alert" id={errorFieldId}> <lift:Msg id="username" /></div>
      }

    }

    def check_email(in:String, field: String, errorFieldId: String) = {

      val email = in.trim
      val user : User = User.createRecord.email(email)

      user.email.validate match{
        case Nil =>
          //<div class="alert alert-danger" role="alert">
          <div id={errorFieldId}> <span class="glyphicon glyphicon-ok"></span> </div>
        case errors : List[FieldError] =>
          S.error(errors)
          <div class="alert alert-danger" role="alert" id={errorFieldId}> <lift:Msg id="email" /></div>
      }

    }

    def check_passwd(in:String, field: String, errorFieldId: String) = {

      val passwd = in
      val user : User = User.createRecord.password(passwd)

      user.password.validate match{
        case Nil =>
          <div id={errorFieldId}> <span class="glyphicon glyphicon-ok"></span> </div>
        case errors : List[FieldError] =>
          S.error(errors)
          <div class="alert alert-danger" role="alert" id={errorFieldId}> <lift:Msg id="password" /></div>
      }

    }

    "@username [placeholder]" #> ( S.?("signup.username.placeholder") )andThen
    "#username"               #> ajaxLiveText( "", username => Replace( "username_error", check_username( username,"Nickname: ", "username_error" ) ),"type" -> "text" ) andThen
    "#username"               #> SHtml.text( username, username = _, "maxlength" -> "40" ) andThen
    "@email [placeholder]"    #> ( S.?("signup.email.placeholder") )andThen
    "#email"                  #> ajaxLiveText( "", email => Replace( "email_error", check_email( email,"Email: ", "email_error" ) ),"type" -> "text" ) andThen
    "#email"                  #> SHtml.text(email, email = _, "maxlength" -> "40") andThen
    "@passwd [placeholder]"   #> ( S.?("signup.passwd.placeholder") )andThen
    "#passwd"                 #> ajaxLiveText( "", passwd => Replace( "passwd_error", check_passwd( passwd, "Password: ", "passwd_error" ) ), "type" -> "password" ) andThen
    "#passwd"                 #> SHtml.password(passwd, passwd = _, "maxlength" -> "40") andThen
    "#recaptcha"              #> captchaXhtml() andThen
    "@recaptcha_response_field [placeholder]"   #> ( S.?("recaptcha.instructions") )andThen
    "button *+"               #> SHtml.hidden(process)

  }


}

