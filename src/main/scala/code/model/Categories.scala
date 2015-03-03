package code
package model


import org.bson.types.ObjectId
import scala.xml.{Text}
import net.liftweb._
import common._
import http.S.{?,??}
import http.{S}
import record.field._
import mongodb.record.{MongoRecord,MongoMetaRecord}
import mongodb.record.field._
import mongodb.record._
import mongodb.BsonDSL._
import util._
import Helpers._


class Categories private() extends MongoRecord[Categories] with ObjectIdPk[Categories] {


  def meta = Categories

  object slug extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("slug")
    override def displayName = "Slug"
    override def validations =
      valMinLen(2, S.?("products.slug.valminlen")) _ ::
      valMaxLen(64, S.?("products.slug.valmaxlen")) _ ::
      super.validations
  }

  object name extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("name")
    override def displayName = "Name"
    override def validations =
      valMinLen(2, S.?("products.name.valminlen")) _ ::
      valMaxLen(64, S.?("products.name.valmaxlen")) _ ::
      super.validations
  }

  object parentId extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("parentId")
    override def displayName = "ParentId"
  }

  object description extends TextareaField(this, 255) {
    override def uniqueFieldId: Box[String] = Full("description")
    override def displayName = "Description"
    override def validations =
      valMinLen(2, S.?("products.description.valminlen")) _ ::
      valMaxLen(255, S.?("products.description.valmaxlen")) _ ::
      super.validations
  }

  object timecreated extends LongField(this){
    override def defaultValue = millis
  }

  object timeupdate extends LongField(this){
    override def defaultValue = millis
  }

  object activate extends BooleanField(this){
    override def defaultValue = false
  }


}


object Categories extends Categories with MongoMetaRecord[Categories] with Loggable {


  override def collectionName = "products"
  override def fieldOrder = List(id, slug, name, parentId, description, timecreated, timeupdate, activate)


}
