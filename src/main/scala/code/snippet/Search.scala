package code
package snippet


import net.liftweb._
import common.{Loggable}
import util.Helpers._
import http.{SHtml}
import http.js.{JE}
import comet._
import lib._
import net.liftweb.http.js.{JsCmd, JE}
import net.liftweb.json.JsonAST._
import net.liftweb.http.js.JsCmds._


class Search extends Loggable {


	def render = {

	    def validate(json: JValue) : JsCmd = {

	      if( ("""$("#term").val()""").trim.nonEmpty ) {
	        SearchTerms.search( json )
	      }

	      Noop

	    }

    	"#term [onkeyup]" #> ( SHtml.jsonCall( JE.Call(""" Elastisearch.searchTerm """), validate _ ) )  

	}


}