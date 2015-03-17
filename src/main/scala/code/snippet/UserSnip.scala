package code
package snippet


import net.liftweb._
import common._
import util._
import model.{User}
import scala.xml._
import util.Helpers._


class UserSnip extends Loggable {


	def list = {

	    val users = User.findAll

	    "tbody tr * *" #> users.map { 
	    	user =>
	    		".id *"          #> Text( user.id.toString ) &
	      		".username *"    #> Text( user.username.get ) &
	      		".email *"       #> Text( user.email.toString ) &
	      		".timecreated *" #> toInternetDate( user.timecreated.get ) &
	      		".verified *"    #> Text( user.verified.toString )

	    }
  	}


}