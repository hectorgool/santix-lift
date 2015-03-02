package code
package lib


import net.liftweb.common.{Loggable, Full, Box, Logger}
import com.twitter.finagle.ServiceFactory
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.Http
import com.twitter.conversions.time._
import net.liftweb.util.Props
//import myScalaz.Boxes._
import org.jboss.netty.buffer.ChannelBuffers
import net.liftweb.json._
import org.jboss.netty.util.CharsetUtil._
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import org.jboss.netty.util.CharsetUtil
import net.liftweb.http.NamedCometListener
import comet._
import xml.Text
import com.twitter.util.Future
import net.liftweb.http.S


object ElasticSearch extends Loggable {


  val host= Props.get("elasticsearch.host")
  val port= Props.get("elasticsearch.port")
  val hostAndPort = "%s:%s".format(host.openOr("localhost"), port.openOr("9200"))
  logger.debug("host port is %s" format hostAndPort)

  /**
   * You init a clientFactory only once and use it several times across your application
   */
  val clientFactory: ServiceFactory[HttpRequest, HttpResponse] = ClientBuilder()
    .codec(Http())
    .hosts(hostAndPort)
    .tcpConnectTimeout(1.second)
    .hostConnectionLimit(1)
    .buildFactory()

  /**
   * The path to the elastic search table (index) and the json to send
   */
  def documentSave(path: List[String], json: JValue) ={
    logger.debug("json is %s" format json)
    val req = requestBuilderPut(path, json)
    sendToElastic(req)
  }

  /**
   * Generate a request to send to ElasticSearch
   * @param path the path to your document, as a list
   * @param json ths JValue representing the payload, i.e. ("id" -> "1") ~ ("part_number" -> "02k7011")
   * @return a request object
   */
  def requestBuilderPut(path: List[String], json: JValue): DefaultHttpRequest = {
    val payload = ChannelBuffers.copiedBuffer( compact(render(json))  , UTF_8)
    val _path = path.mkString("/","/","")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, _path)
    request.setHeader("User-Agent", "Finagle - Liftweb")
    request.setHeader("Host", host.openOr("localhost")) // the host.openOr("localhost") can be replace for host.openOr("default value here")
    request.setHeader(CONTENT_TYPE, "application/json")
    request.setHeader(CONNECTION, "keep-alive")
    request.setHeader(CONTENT_LENGTH, String.valueOf(payload.readableBytes()));
    request.setContent(payload)
    logger.debug("Sending request:\n%s".format(request))
    logger.debug("Sending body:\n%s".format(request.getContent.toString(CharsetUtil.UTF_8)))
    request
  }

  /**
   * Generate a request to search the Elastic Search instance
   * @param path the path to your document, as a list
   * @param json ths JValue representing the payload, i.e. ("id" -> "1") ~ ("part_number" -> "02k7011")
   * @return a request object
   */
  def requestBuilderGet(path: List[String], json: JValue): DefaultHttpRequest = {
    val payload = ChannelBuffers.copiedBuffer( compact(render(json))  , UTF_8)
    val _path = path.mkString("/","/","")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, _path)
    request.setHeader("User-Agent", "Finagle - Liftweb")
    request.setHeader("Host", host.openOr("localhost"))
    request.setHeader(CONTENT_TYPE, "application/x-www-form-urlencoded")
    request.setHeader(CONTENT_LENGTH, String.valueOf(payload.readableBytes()));
    request.setContent(payload)
    logger.debug("Sending request:\n%s".format(request))
    logger.debug("Sending body:\n%s".format(request.getContent.toString(CharsetUtil.UTF_8)))
    request
  }

  /**
   * Generate a request to delete data
   * @param path the path to your document, as a list
   * @return a request object
   */
  def requestBuilderDelete(path: List[String]): DefaultHttpRequest = {
    val _path = path.mkString("/","/","")
    val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE, _path)
    request.setHeader("User-Agent", "Finagle - Liftweb")
    request.setHeader("Host", host.openOr("localhost"))
    logger.debug("Sending request:\n%s".format(request))
    logger.debug("Sending body:\n%s".format(request.getContent.toString(CharsetUtil.UTF_8)))
    request
  }

  /**
   * Take a request ans send it
   * @param request The request
   * @return
   */
  def sendToElastic(request: DefaultHttpRequest): Future[HttpResponse] ={
    val client = clientFactory.apply()()
    logger.debug("Request to send is %s" format request)
    val httpResponse = client(request)

    httpResponse.onSuccess{
      response =>
        logger.debug("Received response: " + response)
        client.release()
        Future.Done
    }.onFailure{err =>
      logger.error(err)      
      NamedCometListener.getDispatchersFor(Full("result")).foreach{
        actor => actor.map(_ ! ErrorMessage("error", S.?("elasticsearch.error") ) )
      }
      client.release()
      Future.Done
    }
  }


  /**
   * Deletes all the indeces from elastic search
   * @return
   */
  def unsafeDeleteAllIndeces() ={
    val req = requestBuilderDelete(List())
    sendToElastic(req)
  }

  def documentSearch(json: JValue): Future[HttpResponse] ={
    val req = requestBuilderGet(List("santix", "items", "_search"), json)
    sendToElastic(req)
  }

  def documentSearch(index: String, indexType: String, json: JValue): Future[HttpResponse] ={
    val req = requestBuilderGet(List( index, indexType, "_search"), json)
    sendToElastic(req)
  }


}
