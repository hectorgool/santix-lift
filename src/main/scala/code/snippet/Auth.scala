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


class Auth extends Loggable with UserHelper with ReCaptcha {


  def render = {

    var email = ""
    var nickname = ""
    var passwd = ""
    var count = 0
    var remember = User.loginCredentials.is.isRememberMe
    val from = S.referer openOr "/"

    def process() : JsCmd = {

      (for {
        r <- S.request if r.post_?
        name <- S.param("email").map(_.trim.toLowerCase) ?~! "email Not Found"
        candidate <- S.param("password").map(_.trim) ?~! "passwdord Not Found"
        user <- User.find("email", name)
        if isMatch( candidate, user.password.get )
      } yield user) match {
        case Full(user) =>          
          User.loginCredentials(LoginCredentials(email, remember))
          User.logUserIn(user, true)
          if (remember)
            User.createExtSession(user.id.get)
          S.redirectTo(from)
        case _ =>
          count += 1
          if( count > 1 ){
            //logger.info("\n\n\n count ----> "+ count +"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n\n")//depurar
            S.error("loginError", <div> <span class="glyphicon glyphicon-warning-sign"></span> <span data-lift="Loc.i">login.error.unknown-credentials</span></div>)
            JE.JsRaw( "errorShake();" ).cmd
          }
          else{
            S.error("loginError", <div> <span class="glyphicon glyphicon-warning-sign"></span> <span data-lift="Loc.i">login.error.unknown-credentials</span></div>)
          }
      }

    }

    "#email"    #> SHtml.text(email, email = _) andThen
    "#passwd"   #> SHtml.password(passwd, passwd = _) andThen
    "#remember" #> SHtml.checkbox(remember, remember = _) andThen
    "button *+" #> SHtml.hidden(process)


  }


}
