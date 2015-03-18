package code
package snippet


import net.liftweb._
import common._
import util._
import model.{User}
import scala.xml._
import util.Helpers._
import org.joda.time.format.DateTimeFormat
import com.foursquare.rogue.LiftRogue._


class UserSnip extends Loggable {


	private val fmt = DateTimeFormat.forPattern("dd/MM/yy")

	def list = {

	    val users = User.findAll
	    //def users = User.where(_.verified eqs true).orderAsc(_.timecreated)

	    "tbody tr * *" #> users.map { 
	    	user =>
	    		".id *"          #> Text( user.id.toString ) &
	      		".username *"    #> Text( user.username.get ) &
	      		".email *"       #> Text( user.email.toString ) &
	      		".timecreated *" #> fmt.print( user.timecreated.get ) &
	      		".verified *"    #> Text( user.verified.toString )

	    }
  	}


}