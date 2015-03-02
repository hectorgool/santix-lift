package code
package model


import net.liftweb.record.field._
import net.liftweb.mongodb.{JsonObject,JsonObjectMeta}
import net.liftweb.mongodb.record.{MongoRecord,MongoMetaRecord,MongoId}
import net.liftweb.mongodb.record.field._
import net.liftweb.common._
import org.bson.types.ObjectId
import net.liftweb.util._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.BsonDSL._
import scala.xml.{NodeSeq, Text}
import Helpers._


class ChangePassword private() extends MongoRecord[ChangePassword] with ObjectIdPk[ChangePassword] {


  def meta = ChangePassword

  object userId extends ObjectIdRefField(this, User) {
    override def options = User.findAll.map(user => (Full(user.id.get), user.username.get))
  }

  object timerequest extends LongField(this){
    override def defaultValue = millis
  }

  object timechange extends LongField(this){
    override def defaultValue = millis
  }

  object changepassword extends BooleanField(this){
    override def defaultValue = false
  }


}


object ChangePassword extends ChangePassword with MongoMetaRecord[ChangePassword] with Loggable {


  override def collectionName = "changePassword"
  override def fieldOrder = List(id, userId, timerequest, timechange, changepassword)


}


