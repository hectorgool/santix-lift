package code
package snippet


import net.liftweb._
import common._
import util._
import model.{Items,User}
import scala.xml._
import util.Helpers._
import org.joda.time.format.DateTimeFormat
import http._
import de.weltraumschaf.speakingurl._


class ItemSnip extends StatefulSnippet with Loggable {


	private var name = ""
	private val fmt = DateTimeFormat.forPattern("dd/MM/yy")
	private var editItem = Items.createRecord

  	def dispatch = {
    	case "list"     => list _
    	case "editForm" => editForm _
  	}

	def list( xhtml: NodeSeq ): NodeSeq = User.currentUser match {

	    case Full(user)  => {

	    	def items = Items.findAll( "userId", user.id.get )

	      	items.flatMap( item => {
		        ( 
		        	".id *"            #> Text( item.id.toString ) &
		        	".name *"          #> Text( item.name.get ) &
		        	"a [href]"         #> "/item/%s".format(item.slug) &
		      		".price *"         #> Text( item.price.toString ) &
		      		".timecreated *"   #> fmt.print(item.timecreated.get) &
		      		".activate *"      #> Text( item.activate.toString ) &		      		
		          	"a #edit"          #> link( "/item/edit",() => edit(item), Text(S.?("item.edit")))
		          	//"a #delete"      #> link("/item/delete", () => delete(post), Text("item.delete"))
		        ).apply(xhtml)
	      	})

	    }

	    case _ => {
	      S.redirectTo("/login")
	    }

	}

  	def editForm(xhtml: NodeSeq): NodeSeq = {
	    (
	      "#name"        #> editItem.name.toForm &
	      "#description" #> (( editItem.description.toForm) ++ SHtml.hidden( save _ ) )
	    ).apply(xhtml)
  	}

  	def edit( item : Items ) = {
    	editItem = item //objeto to update
  	}

  	def save = {
    	val slugger = Slug.Builder.newBuiler().create();
    	editItem.slug( slugger.get(name) )
    	editItem.save()
    	S.redirectTo("/")
  	}


}