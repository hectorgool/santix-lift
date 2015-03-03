package code
package comet


import lib._
import model._
import net.liftweb._
import actor.LiftActor
import common._
import common.Full
import http.NamedCometListener
import util.Schedule
import util.Helpers._


object InboxActor extends LiftActor with Logger{


  override def messageHandler = {
    
    case username @ Username(u) if u.length > 0 => {
      //save comment in database
      //info("\n\n --> u: " + u +"\n" )
      //println("\n\n --> u: " + u +"\n" )
      //println("\n\n --> username: " + username +"\n" )

      /*
      user.username.validate match{

        case Nil =>
          val newuser : User = user.saveMe //save new user in database
          User.logUserIn(newuser, false)
          S.notice(S.?("welcome.message", username))
          logger.info("\n Nil ~~> !\n")//depurar
          S.redirectTo("/")

        case errors : List[FieldError] =>
          S.error(errors)
          logger.info("\n errors ~~> "+ errors +"!\n")//depurar
          count += 1
          if( count > 1 ){
            JE.JsRaw( "errorShake();" ).cmd
            logger.info("\n count ~~> "+ count +"!\n")//depurar
          }
          S.error( "usernameError", <div class="alert alert-danger" role="alert"> <lift:Msg id="username" /> </div>)
          JE.JsRaw( "Recaptcha.switch_type('image');" ).cmd

      }
      */

      //NamedCometListener.getDispatchersFor(Full("comment")).foreach(actor => actor.map( _ ! Ping() ))
      NamedCometListener.getDispatchersFor(Full("username-comet")).foreach(actor => actor.map(_ ! username))

    }

    /*
    case Term(t) => {

      SearchTerms.search( t )

    }
    */

    case j @ ElasticSearchResult(_) => {

      NamedCometListener.getDispatchersFor(Full("result")).foreach(actor => actor.map( _ ! j ))
      
    }

    case m @ Message(_) => {

      NamedCometListener.getDispatchersFor(Full("term")).foreach(actor => actor.map(_ ! m))
      
    }
    
    case j @ MsgJson(_) => {
      //println("\n-----------------------------------------> a MsgJson\n" + j )
      NamedCometListener.getDispatchersFor(Full("term")).foreach(actor => actor.map(_ ! j))

    }
    /*
    case c @ MsgComment(userId, msg) if msg.length > 0 => {
      //save comment in database
      val comment = Comments.createRecord
      comment.comment(msg)
      comment.userId(userId)
      comment.timecreated(millis)
      comment.published(true)
      comment.save

      info("\n\n --> comment.id: " + comment.id +"\n" )
      NamedCometListener.getDispatchersFor(Full("comment")).foreach(actor => actor.map( _ ! Ping() ))
    }
    */

  }


}