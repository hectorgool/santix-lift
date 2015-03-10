package code


import model._
import net.liftweb.json._
//import org.bson.types.ObjectId


package object comet{


  case class Username( t:String )

  case class Term( t:String )
  //case class Messages( msg: List[MsgElasticSearch] )
  case class Message(msg: List[String])

  //case class MsgElasticSearch( _id:String, name:String, description:String, userId:String, timecreated: Long, published: Boolean )
  
  case class ElasticSearchResult( json: JValue )

  //json ElasticSearch results
  case class Results( hits:HitsList )
  case class HitsList( hits:List[Hit] )
  case class Hit( _id:String, _source:Source)
  case class Source( name:String, slug:String, description:String, username:String )
  case class Highlight( name:List[String] )

  case class MsgJson( json: JValue )

  //case class MsgComment( userId: ObjectId, comment:String )
  //case class MsgCommentId( userId: ObjectId )
  case class Ping()

  case class ErrorMessage( error:String, message:String )


}
