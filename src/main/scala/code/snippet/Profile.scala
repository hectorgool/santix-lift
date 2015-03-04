package code
package snippet

import net.liftweb._
import common._
import lib._
import http._
import http.js.{JsCmd}
import model._
import util._
import util.Helpers._
import http.js.JsCmds.{Replace}
import de.weltraumschaf.speakingurl._
import scala.xml._
import org.joda.time.format.DateTimeFormat
import code.comet._
import sitemap._
import net.liftweb.common.Box.option2Box
import net.liftweb.http.js.{JsCmd, JsCmds}
import js.JE._
import net.liftweb.json._


object Profile{
  

  	val profileParamMenu = Menu.param[User]("User", "Profile", 
    	User.findByUsername _, _.username.get
    ) / *

  	lazy val profileLoc = profileParamMenu.toLoc

  	def username = User.currentUser match {

		case Full(user) if user.verified == true => {		    
	    	"#username * *"        #> user.username &
	    	"a #username [href]" #> "/%s".format( user.username )
		}
		case _ => {
			S.redirectTo("/login")
		}

	}
  

}

class Profile( unParam: User ) extends StatefulSnippet with Logger{


	implicit val formats = net.liftweb.json.DefaultFormats
	case class Item( itemId: String )

	private val whence = S.referer openOr "/"

	def dispatch = {
		case "name"     => name
		case "list"     => list
		case "username" => username
	}

  	def name = {
  		"#name *" #> unParam.username
  	}

  	def username = User.currentUser match {

		case Full(user) if user.verified == true => {		    
	    	"#username * *"        #> user.username &
	    	"a #username [href]" #> "/%s".format( user.username )
		}
		case _ => {
			S.redirectTo("/login")
		}

	}

	def list(xhtml: NodeSeq): NodeSeq = {
	
	    def docs = Items.findAll
	   	   	
	    docs.flatMap( item => {	    	
	    	(
			//"#addToCart [onclick]" #> SHtml.ajaxInvoke(() => TheCart.addToCart( item )) &
	        "#name *"              #> item.name &
	        "img [alt]"            #> item.name &
	        "a [href]"             #> "/item/%s".format(item.slug) &
	        "#price *"             #> item.price.toString &
	        "#description *"       #> item.description //&
	        //"#twitter *"     #> item.id &
	        //"#facebook *"    #> item.id &
	        //"#pinterest *"   #> item.id &
	        //"#google-plus *" #> item.id
	        ).apply(xhtml)
	    })	    

	}


}
