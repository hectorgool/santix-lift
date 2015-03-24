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

  	
	private val whence = S.referer openOr "/"
  	private val fmt = DateTimeFormat.forPattern("dd MMMM yy, HH:mm:ss")
  	private val slugger = Slug.Builder.newBuiler().create();//slug object

	def render = <div>param: {userItem._1.username.get}</div>

	def view = {

		Items.findSlug( userItem._2.slug.get ) match{

		    case Full(item) if item.activate.get == true => {
		    	"a #username *"	       #> "@%s".format(userItem._1.username.get) &
		    	"a #username [href]"   #> "/%s".format(userItem._1.username.get) & //beta
		    	"title *"              #> item.name &
		    	"img [alt]"            #> item.name &
	    		"#name"                #> item.name &
	    		"a #morepics [href]"   #> "/catalog/%s/item/%s/pics".format( userItem._1.username.get, item.slug.get ) &
	    		"#price *"             #> item.pricing.get.price.get &
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
		    	"title *"       #> item.name &
		    	"img [alt]"     #> item.name &
	    		"#name"         #> item.name &
	    		"#price *"      #> item.pricing.get.price.get &
	    		".modal-title"  #> item.name &
				".img"          #> { (item.images.get.map(image =>
					"img [src]" #> "/classpath/assets/img/%s".format(image)
				))} &	    		
	    		"#timecreated"  #> item.timecreated
		    }
		    case _ =>{
		      	Text(S.?("document.not.found"))
		    }

		}

	}


}


object ViewUserItem{
 
		
 	lazy val menu = Menu.params[(User, Items)]("User Slug", "User Slug",
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

 	lazy val menuCatalogItem = Menu.params[(User, Items)]("Catalog Item", "Catalog Item",
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
	) / "catalog" / * / "item" / * 	

 	lazy val menuCatalogItemPics = Menu.params[(User, Items)]("Catalog Item Pics", "Catalog Item Pics",
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
	) / "catalog" / * / "item" / * / "pics"


}
