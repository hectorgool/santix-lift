package code
package snippet


import net.liftweb._
import common._
import lib._
import http._
import http.js.{JsCmd}
import model._
import snippet._
import util._
import util.Helpers._
import http.js.JsCmds.{Replace}
import de.weltraumschaf.speakingurl._
import scala.xml._
import org.joda.time.format.DateTimeFormat
import sitemap._
import common.Box.option2Box
import http.js.{JE}
import http.js.JsCmds.{SetHtml,Noop}
import json._
import json.JsonDSL._


class CreateItem extends StatefulSnippet with UserHelper with Loggable{


	def dispatch = {
		case "add" => add
	}

	private var name = ""
	private var price = ""
	private var cost = ""
	private var weight = ""
	private var reference = ""
	private var description = ""

	def add = User.currentUser match {

		case Full(user) if user.verified == true => {

			def process() : JsCmd = {

				implicit val formats = DefaultFormats

				val slugger = Slug.Builder.newBuiler().create();//slug object

				//depurar
				val pics = List("pic1.png", "pic2.png", "pic3.png", "pic4.png" , "pic5.png",
								"pic6.png", "pic7.png", "pic8.png", "pic9.png" , "pic10.png"
					)

		      	val item : Items = Items.createRecord
		        	.name( name )
		        	.slug( slugger.get(name) )//slug name
		        	.price( emptyToZero( price ) )
		        	.cost( emptyToZero( cost ) )
		        	.weight( emptyToZero( weight ) )
		        	.reference( reference )
		        	.description( description )
		        	.userId( user.id.get )
		        	.timecreated( millis )
		        	.activate( true )

				logger.info("\n item ~~> "+ item +"!\n")//depurar
		        
		      	item.name.validate ::: item.reference.validate ::: item.description.validate match{

			        case Nil =>
			        	val newItem : Items = item.save() //save new user in database
			          	//logger.info("\n Nil ~~> !\n")//depurar
			          	logger.info("\n newItem ~~> "+ newItem +"!\n")//depurar
			          	indexItem( item, user ) //index item in Elasticsearch
			          	S.redirectTo("/admin")
			          	Templates( "templates-hidden" :: "admin" :: "items" :: Nil ).map(ns => SetHtml("admin-content", ns)) openOr Noop //beta
			          	Noop

			        case errors : List[FieldError] =>
			        	logger.info("\n errors ~~> !\n" + errors )//depurar
			          	S.error(errors)
			          	logger.info("\n errors ~~> "+ errors +"!\n")//depurar
			          	//S.error("createError", <div> <span class="glyphicon glyphicon-warning-sign"></span> <span data-lift="Loc.i">inputs.errors</span></div>)
						S.error("nameError", <span data-lift="Msg?id=name;errorClass=errorSignup"> </span> )
						S.error("descriptionError", <span data-lift="Msg?id=description;errorClass=errorSignup"> </span>)
						S.error("referenceError",  <span data-lift="Msg?id=reference;errorClass=errorSignup"> </span> )

		      	}
		      	
			}
		    
		    "@name [placeholder]"        #> ( S.?("item.name.placeholder") )andThen
		    "#name"                      #> SHtml.text( name, name = _ ) andThen
		    "@price [placeholder]"       #> ( S.?("item.price.placeholder") )andThen
		    "#price"                     #> SHtml.text( price, price = _ ) andThen
		    "@cost [placeholder]"        #> ( S.?("item.cost.placeholder") )andThen
		    "#cost"                      #> SHtml.text( cost, cost = _ ) andThen	
		    "@weight [placeholder]"      #> ( S.?("item.weight.placeholder") )andThen
		    "#weight"                    #> SHtml.text( weight, weight = _ ) andThen	
		    "@description [placeholder]" #> ( S.?("item.description.placeholder") )andThen
		    "@description"               #> SHtml.textarea( description, description = _ ) andThen	    
			"@reference [placeholder]"   #> ( S.?("item.reference.placeholder") )andThen
		    "#reference"                 #> SHtml.text( reference, reference = _ ) andThen	    
		    "button *+"                  #> SHtml.hidden(process)

		}

		case _ => {
			//Text("error")
			S.redirectTo("/login")
		}

	}

  	//beta
  	def indexItem( item : Items, user: User ) = {

    	val jsonTerm =
      		("name"        -> item.name.toString )~
      		("slug"        -> item.slug.toString )~
      		("description" -> item.description.toString )//~
      		//("username"    -> User.getUsername(user.id.get) )
    
    		ElasticSearch.documentSave( List( "santix", "items", item.id.toString ), jsonTerm )

  	}


}


class ViewItems extends StatefulSnippet with UserHelper with Loggable{


	val fmt = DateTimeFormat.forPattern("dd/MMMM/yy")

	def dispatch = {
		case "admin" => admin _
	}	

	def admin(xhtml: NodeSeq): NodeSeq = {
	
	    //def docs = Products.fetch(50)
	    def docs = Items.findAll
	   	   	
	    docs.flatMap( item => {	    	
	    	(

	        "#name *"        #> item.name &
	        "#name [href]"   #> "/items/%s".format(item.slug) &
	        "#price *"       #> item.price &
	        "#description *" #> item.description &
	        "#timecreated *" #> fmt.print(item.timecreated.get)

	        ).apply(xhtml)
	    })	    

	}


}
