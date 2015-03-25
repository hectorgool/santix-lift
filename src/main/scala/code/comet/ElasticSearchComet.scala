package code
package comet


import net.liftweb._
import http._
import http.js.{JsCmds, JsCmd, JE}
import http.js.JE._
import http.js.JsCmds.{Run, Replace, SetHtml}
import json.{render => jsonRender}
import json._
import json.DefaultFormats
import json.JsonAST.JValue
import util.StringHelpers._
import scala.xml.{NodeSeq}


class ElasticSearchComet extends NamedCometActorTrait{


  implicit val formats = DefaultFormats

  override def lowPriority = {
  
    case j @ ElasticSearchResult( json ) => {
      unregisterFromAllDependencies//beta
      // remove all the dependencies for the old cart
      // from the postPageJavaScript
      theSession.clearPostPageJavaScriptForThisPage()//beta
      partialUpdate(SetHtml("contenido", pagesHtml( json )))
    }    

    case e @ ErrorMessage( "error", message ) => {
      partialUpdate(SetHtml("contenido", pageError( message )))
    }

  }

  def pagesHtml( json : JValue ): NodeSeq = {
  
    val results = json.extract[Results]

    val html = 
      <div class="document">

        <div class="row">
          <div class="col-md-12">
            <h3>
              <a class="link" href=""></a> @ <a class="username" href=""></a>
            </h3>
          </div>
        </div>

        <div class="row">
          <div class="col-md-12">
            <div class="description">
            </div>
          </div>
        </div>

      </div>

    def css =
      ".document * *" #> results.hits.hits.map( document => 
        ".link *" #> document._source.name &
        ".link [href]" #> "/catalog/%s/item/%s".format( document._source.username, document._source.slug ) &
        ".username *" #> document._source.username &
        ".username [href]" #> "/catalog/%s".format( document._source.username ) &        
        ".description" #> document._source.description
      )
      css(html)

  }

  def pageError( error : String ): NodeSeq = {

    println(  "error*************:" + error )
    val html = <p></p>

    def  css = "<p>" #> error
    css(html)

  }

  def render = {

    "#render" #> ""

  }


}
