/*
https://groups.google.com/forum/#!msg/liftweb/74cu01AACm8/1Ju8fJ1W9CAJ

class User private() extends ProtoAuthUser[User] with ObjectIdPk[User] {

It's not possible to override any of the fields defined in ProtoAuthUser. 
What you should do is create your own User that extends MongoAuthUser and 
copy the fields from ProtoAuthUser [1] into there. 
Then you can edit them however you like.

https://github.com/eltimn/lift-mongoauth/blob/master/src/main/scala/net.liftmodules/mongoauth/AuthUser.scala

*/

package code
package model


import java.util.UUID
import org.bson.types.ObjectId
import net.liftweb._
import common._
import mongodb.record._
import mongodb.record.field._
import record.field._
import util.Helpers

import net.liftmodules.mongoauth._
import net.liftmodules.mongoauth.field._
import net.liftmodules.mongoauth.model._
import net.liftweb.mongodb.record.field._
import Helpers._
import util.FieldError
import util.Props
import scala.xml.{NodeSeq, Text}
import http.{CleanRequestVarOnSessionTransition, LiftResponse, RequestVar, S, SessionVar, Req}
import lib._


class User private () extends MongoAuthUser[User] with ObjectIdPk[User] {


  import net.liftmodules.mongoauth.field.PasswordField
  def meta = User

  def userIdAsString: String = id.toString

  lazy val authPermissions: Set[Permission] = Set.empty

  lazy val authRoles: Set[String] = Set.empty    

  object firstname extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("firstname")
    override def displayName = "Firstname"
    override def validations =
      valMinLen(2, S.?("user.firstname.valminlen")) _ ::
        valMaxLen(64, S.?("user.firstname.valmaxlen")) _ ::
        super.validations
  }

  object lastname extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("lastname")
    override def displayName = "Lastname"
    override def validations =
      valMinLen(2, S.?("user.lastname.valminlen")) _ ::
        valMaxLen(64, S.?("user.lastname.valmaxlen")) _ ::
        super.validations
  }

  object username extends StringField(this, 32) {
    override def uniqueFieldId: Box[String] = Full("username")
    override def displayName = "Username"
    override def setFilter = trim _ :: super.setFilter
    private def valUnique(msg: => String)(value: String): List[FieldError] = {
      if (value.length > 0)
        meta.findAll(name, value).filterNot(_.id.get == owner.id.get).map(u =>
        FieldError(this, Text(msg))
      )
      else
        Nil
    }
    override def validations =
      valUnique(S ? "user.username.valunique") _ ::
      valMinLen(3, S ? "user.username.valminlen") _ ::
      valMaxLen(32, S ? "user.username.valmaxlen") _ ::
      super.validations
  }

  object email extends EmailField(this, 254) {
    override def uniqueFieldId: Box[String] = Full("email")
    override def displayName = "Email"
    override def setFilter = trim _ :: toLower _ :: super.setFilter
    private def valUnique(msg: => String)(value: String): List[FieldError] = {
      owner.meta.findAll(name, value).filter(_.id.get != owner.id.get).map(u =>
      FieldError(this, Text(msg))
      )
    }
    override def validations =
      valUnique("user.email.valunique") _ ::
      valMaxLen(254, "user.email.valmaxlen") _ ::
      super.validations
  }  

  object password extends PasswordField(this, 8, 254){
    override def uniqueFieldId: Box[String] = Full("password")
    override def displayName = "Password"
    override def validations =
      valMinLen(8, S.?("user.password.valminlen")) _ ::
      valMaxLen(64, S.?("user.password.valmaxlen")) _ ::
      super.validations
  }

  // email address has been verified by clicking on a LoginToken link
  object verified extends BooleanField(this) {
    override def displayName = "Verified"
  }
   
  object permissions extends PermissionListField(this)

  object roles extends StringRefListField(this, Role) {
    def permissions: List[Permission] = objs.flatMap(_.permissions.get)
    def names: List[String] = objs.map(_.id.get)
  }

  object uniqueId extends UniqueIdField(this, 32)

  object timecreated extends LongField(this){
    override def defaultValue = millis
  }


}

object User extends User with ProtoAuthUserMeta[User] with Loggable{


  import net.liftmodules.mongoauth.field.PasswordField

  override def collectionName = "users"

  override def fieldOrder = List(id, firstname, lastname, email, password, username, uniqueId, timecreated, verified)

  def findByEmail(in: String): Box[User] = find(email.name, in)
  
  def findByUsername(in: String): Box[User] = find(username.name, in)

  def findUser(in: String): Box[User] = User.find(email.name, in ) or User.find(in) or User.find(username.name, in )

  def findUniqueId(in: String): Box[User] = User.find(uniqueId.name, in )

  def findByStringId(id: String): Box[User] =
    if (ObjectId.isValid(id)) find(new ObjectId(id))
    else Empty

  override def onLogIn: List[User => Unit] = List(user => User.loginCredentials.remove())

  override def onLogOut: List[Box[User] => Unit] = List(
    x => logger.debug("User.onLogOut called."),
    boxedUser => boxedUser.foreach { u =>
      ExtSession.deleteExtCookie()
    }
  )

  def loginUser(user: User) = {
    logUserIn(user, true, true)
  }

  /**
   * Create a new user
   */
  def create(email: String) = {
    val user = User.createRecord
    user.email(email)
    user
  }

  // send an email to the user with a link for logging in
  def sendLoginToken(user: User): Unit = {

    import net.liftweb.util.Mailer._

    val emailTo     = user.email.get
    val siteName    = Props.get("configuration.system.sitename") openOr ""  //depurar
    val systemEmail = Props.get("configuration.system.email") openOr ""  //depurar
    val sysUsername = Props.get("configuration.system.username") openOr ""  //depurar

    val msgTxt =
      """
        |Someone requested a link to change your password on the %s website.
        |
        |If you did not request this, you can safely ignore it. It will expire 24 hours from the time this message was sent.
        |
        |Follow the link below or copy and paste it into your internet browser.
        |
        |%s
        |
        |Thanks,
        |%s
      """.format(siteName, url(user), sysUsername).stripMargin

    sendMail(
      From(systemEmail),
      Subject("%s Password Help".format(siteName)),
      To(emailTo),
      PlainMailBodyType(msgTxt)
    )

  }

  val resetPassword = Props.get("configuration.system.path") openOr ""  //depurar

  def url(in: User): String = "%s%s/%s".format(S.hostAndPath, resetPassword, in.uniqueId.toString)

  /*
   * ExtSession
   */
  def createExtSession(uid: ObjectId) = ExtSession.createExtSessionBox(uid)

  /*
  * Test for active ExtSession.
  */
  def testForExtSession: Box[Req] => Unit = {
    ignoredReq => {
      if (currentUserId.isEmpty) {
        ExtSession.handleExtSession match {
          case Full(es) => find(es.userId.get).foreach { user => logUserIn(user, false) }
          case Failure(msg, _, _) =>
            logger.warn("Error logging user in with ExtSession: %s".format(msg))
          case Empty =>
        }
      }
    }
  }

  object loginCredentials extends SessionVar[LoginCredentials](LoginCredentials(""))

  object regUser extends SessionVar[User](createRecord.email(loginCredentials.is.email))

  def createForUserId(user: User) {
    user.uniqueId.reset().save()
  }
  
  def createSystemUser : Unit = {

    User.find( "username", "santo" ) match {

      case Full(user) =>
        println("user system already exist!")
      case _ =>

        val hash = PasswordField.hashpw("asdfasdf")
        var user = User.createRecord
          .username("santo")
          .email("santo@santo.com")
          .firstname("Hector")
          .lastname("Gonzalez")
          .password(hash)
          .timecreated(millis)
          .verified(true)
          .permissions(List(Permission("printer", "print"), Permission("user", "edit", "123")))
          .roles(List("admin")).save()

        val superuser = Role.createRecord.id("superuser").permissions(List(Permission.all)).save()
        user.roles(List("superuser"))
        
    }

  }

  object loginContinueUrl extends SessionVar[String]("index")//beta

  def userId(id: String): Option[User] = User.find( "id", id )


}

case class LoginCredentials(email: String, isRememberMe: Boolean = false)
