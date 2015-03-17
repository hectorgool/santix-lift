package code
package snippet


import net.liftweb._
import common._
import util._
import model.{Items}
import scala.xml._
import util.Helpers._
import org.joda.time.format.DateTimeFormat


class ItemSnip extends Loggable {


	//private val fmt = DateTimeFormat.forPattern("dd MM yy, HH:mm:ss")
	private val fmt = DateTimeFormat.forPattern("dd/MM/yy")

	def list = {

	    val items = Items.findAll

	    "tbody tr * *" #> items.map { 
	    	item =>
	    		".id *"            #> Text( item.id.toString ) &
	      		".name *"          #> Text( item.name.get ) &
	      		"a [href]"         #> "/item/%s".format(item.slug) &
	      		".price *"         #> Text( item.price.toString ) &
	      		//".timecreated *"   #> toInternetDate( item.timecreated.get ) &
	      		".timecreated *"   #> fmt.print(item.timecreated.get) &
	      		".activate *"      #> Text( item.activate.toString ) &
	      		"a #edit [href]"   #> "/item/edit/%s".format( item.id.toString ) &
	      		"a #delete [href]" #> "/item/delete/%s".format( item.id.toString )
	    }

  	}


}