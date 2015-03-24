package code
package api


import net.liftweb._
import common._
import http._
import http.rest.RestHelper
import http.LiftRules
import json.JsonDSL._
import util.Helpers._
import model._


object UserRest extends RestHelper with Loggable {


	case class UserData( id: String, name: String, email: String )

  	def init() : Unit = {
    	LiftRules.statelessDispatch.append( UserRest )
  	}

	serve {

		case "api" :: "user" :: "list" :: Nil Get _ =>
			//if User.isLoggedIn =>
			anyToJValue(listAllUsers)
			/*
			println("User.currentUser: " + User.currentUser + "!!!!!!!!!!!!!")
			User.currentUser match {
				case Full(user) if user.verified == true => {		    
			    	anyToJValue(listAllUsers)
				}
				case _ => {
					RedirectResponse("/login")
				}
			}
			*/

		case "api" :: "user" :: "list" :: id :: Nil Get _ =>
			//if User.isLoggedIn =>
				anyToJValue( listUser( id ) )
	}

	def listAllUsers(): List[UserData] = {

		User.findAll.map{
			user => UserData(
				user.id.get.toString,
				user.username.get.toString, 
				user.email.get.toString
			)
		}

	}

	def listUser(id: String): Box[UserData] = {

		for (user <- User.find(id)) yield {
			UserData( user.id.get.toString, user.username.toString, user.email.get.toString )
		}

	}


}