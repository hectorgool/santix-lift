package code
package lib

import net.liftweb._
import common._
import json._
import mongodb._
import util.Props

import com.mongodb.{Mongo, MongoOptions, ServerAddress}
import scala.collection.JavaConversions._


object MongoConfig extends Loggable {

  def init: Unit = {

    val srvr1 = new ServerAddress(
      Props.get("mongo.host", "127.0.0.1"),
      Props.getInt("mongo.port", 27017)
    )
    val srvrs = srvr1 :: Nil
    MongoDB.defineDb(DefaultMongoIdentifier , new Mongo(srvrs), "hemix2")

  }

}