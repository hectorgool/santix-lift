package code
package model


import org.bson.types.ObjectId
import scala.xml.{Text}
import net.liftweb._
import record.field._
import common._
import common.Box.option2Box
import mongodb.record._
import mongodb.record.{MongoRecord,MongoMetaRecord,MongoId}
import mongodb.record.field._
import mongodb.BsonDSL._
import http.S.{?,??}
import http.{S}
import util._
import Helpers._
import com.foursquare._
import rogue.Rogue._
import rogue.LiftRogue._
import rogue.Iter._


class Items private() extends MongoRecord[Items] with ObjectIdPk[Items]{


  def meta = Items

  object slug extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("slug")
    override def displayName = "Slug"
    override def validations =
      valMinLen(2, S.?("Items.slug.valminlen")) _ ::
      valMaxLen(64, S.?("Items.slug.valmaxlen")) _ ::
      super.validations
  }

  object name extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("name")
    override def displayName = "Name"
    override def validations =
      valMinLen(2, S.?("Items.name.valminlen")) _ ::
      valMaxLen(64, S.?("Items.name.valmaxlen")) _ ::
      super.validations
  }

  object price extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("price")
    override def displayName = "Price"
  }  

  object cost extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("cost")
    override def displayName = "Cost"
    //override def validations =
    //  super.validations
  }

  object sku extends StringField(this, 8) {
    override def uniqueFieldId: Box[String] = Full("sku")
    override def displayName = "Sku"
    //override def validations =
    //  super.validations
  }

  object weight extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("weight")
    override def displayName = "Weight"
    //override def validations =
    //  super.validations
  }

  object description extends TextareaField(this, 255) {
    override def uniqueFieldId: Box[String] = Full("description")
    override def displayName = "Description"
    override def validations =
      valMinLen(2, S.?("Items.description.valminlen")) _ ::
      valMaxLen(255, S.?("Items.description.valmaxlen")) _ ::
      super.validations
  }

  object categoryIds extends ObjectIdRefField(this, Categories) {
    override def options = Categories.findAll.map(category => (Full(category.id.get), category.name.get))
  }

  object variants extends TextareaField(this, 255) {
    override def uniqueFieldId: Box[String] = Full("variants")
    override def displayName = "Variants"
    override def validations =
      valMinLen(2, S.?("Items.variants.valminlen")) _ ::
      valMaxLen(255, S.?("Items.variants.valmaxlen")) _ ::
      super.validations
  }

  object items extends MongoListField[Items, String](this)
  
  object stock extends IntField(this) {
    override def uniqueFieldId: Box[String] = Full("stock")
    override def displayName = "Stock"
    //override def validations =
    //  super.validations
  }

  object images extends MongoListField[Items, String](this)

  object reference extends StringField(this, 64) {
    override def uniqueFieldId: Box[String] = Full("reference")
    override def displayName = "Reference"
    override def validations =
      valMinLen(2, S.?("Items.reference.valminlen")) _ ::
      valMaxLen(64, S.?("Items.reference.valmaxlen")) _ ::
      super.validations
  }

  object tags extends MongoListField[Items, String](this)

  object userId extends ObjectIdRefField(this, User) {
    override def options = User.findAll.map(user => (Full(user.id.get), user.username.get))
  }

  object bundle extends BooleanField(this) {
    override def displayName = "Bundle"
  }

  object timecreated extends LongField(this){
    override def defaultValue = millis
  }

  object timeupdated extends LongField(this){
    override def defaultValue = millis
  }

  object activate extends BooleanField(this){
    override def defaultValue = false
  }


}


object Items extends Items with MongoMetaRecord[Items] with Loggable {


  override def collectionName = "Items"
  override def fieldOrder = List(
    id, slug, name, price, cost, sku, weight, description, categoryIds, variants, 
    items, stock, images, reference, tags, userId, bundle, timecreated, activate
    )

  def findBySlug(in: String): Box[Items] = Items.find( "slug", in )//beta

  def findWeight(in: Double): Box[Items] = 
    option2Box( Items.where( _.weight eqs in ) get() )

  def findId(in: ObjectId): Box[Items] = 
    option2Box( Items.where( _.id eqs in ) get() )

  def findSlug(in: String): Box[Items] = 
    option2Box( Items where ( _.slug eqs in ) get() )

  def findSlugValid(in: String): Box[Items] = 
    option2Box( Items where ( _.slug eqs in ) and (_.activate eqs true ) get() )

  def findValid: Box[Items] = 
    option2Box( Items where (_.activate eqs true ) get() )

}


class Shiping private () extends MongoRecord[Shiping] with StringPk[Shiping] {


  override def meta = Shiping

  object weight extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("weight")
    override def displayName = "Weight"
  }

  object dimensions extends BsonRecordField(this, Dimensions){
    override def optional_? = true
  }

}

object Shiping extends Shiping with MongoMetaRecord[Shiping] {


  override def collectionName = "items.shiping"


}


class Dimensions private () extends MongoRecord[Dimensions] with StringPk[Dimensions] {


  override def meta = Dimensions
  
  object width extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("width")
    override def displayName = "Width"
  }

  object height extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("height")
    override def displayName = "Height"
  }

  object depth extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("depth")
    override def displayName = "Depth"
  }


}

object Dimensions extends Dimensions with MongoMetaRecord[Dimensions] {


  override def collectionName = "items.shiping.dimensions"


}


class Pricing private () extends MongoRecord[Pricing] with StringPk[Pricing] {


  override def meta = Pricing
  
  object list extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("list")
    override def displayName = "List"
  }

  object retail extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("retail")
    override def displayName = "Retail"
  }

  object savings extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("savings")
    override def displayName = "Savings"
  }

  object portentage_savings extends DoubleField(this) {
    override def uniqueFieldId: Box[String] = Full("portentage_savings")
    override def displayName = "Portentage Savings"
  }
  

}

object Pricing extends Pricing with MongoMetaRecord[Pricing] {


  override def collectionName = "items.pricing"


}
