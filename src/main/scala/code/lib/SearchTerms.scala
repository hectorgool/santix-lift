package code
package lib


import comet._
import net.liftweb._
import common.{Full,Loggable}
import http.{SHtml,NamedCometListener}
import http.js.{JE}
import json._
import util.Helpers._
import org.jboss.netty.util.CharsetUtil
import com.twitter.util.Future


object SearchTerms extends Loggable {


  def search( json: JValue ): Unit = {

    val response = ElasticSearch.documentSearch( "santix", "items", json )

    response.onSuccess{res =>
      try {

        val jsonElasticSearchResult = parse( res.getContent.toString(CharsetUtil.UTF_8) )
        //InboxActor ! ElasticSearchResult( jsonElasticSearchResult )
        NamedCometListener.getDispatchersFor(Full("result")).foreach(actor => actor.map( _ ! ElasticSearchResult( jsonElasticSearchResult ) ))

      } catch {

        case e: JsonParser.ParseException => 
          "Means the string is not valid JSON"
        case m: MappingException => 
          "Means the string is JSON, but doesn't respect the format of Document"
          logger.error(m.getMessage)

      }

      Future.Done

    }.onFailure{

      err =>
        logger.error(err)
        Future.Done
        
    }

  }


}