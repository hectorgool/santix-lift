package code
package lib

import net.liftweb.util.Mailer._
import javax.mail.internet.MimeMessage
import javax.mail.{Authenticator,PasswordAuthentication}

import net.liftweb._
import common._
import util._


object SmtpMailer extends Loggable {

  def init(): Unit = {

    var isAuth = Props.get("mail.smtp.auth", "false").toBoolean

    Mailer.customProperties = Props.get("mail.smtp.host", "localhost") match {
      case "smtp.gmail.com" =>
        isAuth = true
        Map(
          "mail.smtp.host" -> "smtp.gmail.com",
          "mail.smtp.port" -> "587",
          "mail.smtp.auth" -> "true",
          "mail.smtp.starttls.enable" -> "true")
      case host => Map(
        "mail.smtp.host" -> host,
        "mail.smtp.port" -> Props.get("mail.smtp.port", "25"),
        "mail.smtp.auth" -> isAuth.toString
      )
    }

    if (isAuth) {
      (Props.get("mail.user"), Props.get("mail.password")) match {
        case (Full(username), Full(password)) =>
          Mailer.authenticator = Full(new Authenticator() {
            override def getPasswordAuthentication = new
                PasswordAuthentication(username, password)
          })
        case _ => new Exception("Username/password not supplied for Mailer.")
      }
    }

  }

}