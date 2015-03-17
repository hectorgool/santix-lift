package code
package snippet


import scala.xml.{NodeSeq, Text}
import net.liftweb._
import common._
import http._
import util._
import java.util.Date
import code.lib._
import code.snippet._
import Helpers._
import sitemap._
import code.comet._
import code.model._
import org.joda.time.format.DateTimeFormat
import de.weltraumschaf.speakingurl._
import js.{JsCmd}
import js.JsCmds.{Noop}


class ViewUserItem( userItem: ( User, Items ) ) extends UserHelper with Loggable{


	private var name = ""
  	private var description = ""
  	private var slug = ""
  	
	private val whence = S.referer openOr "/"
	private var editingItem = Items.createRecord
  	private val fmt = DateTimeFormat.forPattern("dd MMMM yy, HH:mm:ss")
  	private val slugger = Slug.Builder.newBuiler().create();//slug object

	def render = <div>param: {userItem._1.username.get}</div>

	def view = {

		Items.findSlug( userItem._2.slug.get ) match{

		    case Full(item) if item.activate.get == true => {
		    	"a #username *"	       #> "@%s".format(userItem._1.username.get) &
		    	"a #username [href]"   #> "/%s".format(userItem._1.username.get) &
		    	"title *"              #> item.name &
		    	"img [alt]"            #> item.name &
	    		"#name"                #> item.name &
	    		"a #morepics [href]"   #> "/item/%s/pics".format(item.slug.get) &
	    		"#price"               #> item.cost &
	    		".modal-title"         #> item.name &
				".img"                 #> { (item.images.get.map(image =>
					"img [src]"        #> "/classpath/assets/img/%s".format(image)
				))} &	    		
	    		"#timecreated"         #> item.timecreated
		    }

		    case _ =>{
		      	Text(S.?("document.not.found"))
		    }

		}

	}

	def adminView = {

		Items.findSlug( userItem._2.slug.get ) match{		

		    case Full(item) => {			    	    	
		    	"title *"      #> item.name &
		    	"img [alt]"    #> item.name &
	    		"#name"        #> item.name &
	    		"#cost"        #> item.cost &
	    		".modal-title" #> item.name &
				".img"    #> { (item.images.get.map(image =>
					"img [src]" #> "/classpath/assets/img/%s".format(image)
				))} &	    		
	    		"#timecreated" #> item.timecreated
		    }
		    case _ =>{
		      	Text(S.?("document.not.found"))
		    }

		}

	}

  	def editForm = {
    	
      	"#editName"        #> editingItem.name.toForm &
      	"#editDescription" #> (( editingItem.description.toForm) ++ SHtml.hidden( save _ ) )    	

  	}	

  	def edit( item : Items ) = {

    	editingItem = item

  	}

  	def save = {

    	editingItem.slug( slugger.get(name) )
    	editingItem.save()
    	S.redirectTo("/")

  	}


}


object ViewUserItem{
 

 	val menu = Menu.params[(User, Items)]("User Slug", "User Slug",
	{
		case username :: slug :: Nil => 
			for {
				u <- User.findByUsername(username)
				i <- Items.findBySlug(slug)
			} yield (u, i)		
		case _ => 
			Empty
	}, 
	{
		case (u, i) => 
			u.username.get.toString :: i.slug.get.toString :: Nil 
	}		
	) / * / *


}