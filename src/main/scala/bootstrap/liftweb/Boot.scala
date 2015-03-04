package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import code.model._
import code.snippet._
import common._
import http._
import sitemap._
import Loc._
import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._
import code.config.MongoConfig
import net.liftmodules.mongoauth._
import net.liftmodules.mongoauth.MongoAuth

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable with Locs{


  def boot {

    // init mongodb
    MongoConfig.init()

    MongoAuth.authUserMeta.default.set(User)

    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    /*
    val entries = List(
      Menu.i("Home") / "index",
      Menu(Loc("Static", Link(List("static"), true, "/static/index"),
	       "Static Content"))
      )
    */

    val entries = List(
      Menu.i("Home") /("index"), // the simple way to declare a menu
      Menu.i("Signup") / "signup" >> RequireNotLoggedIn,
      Menu.i("SignupComet") / "signup-comet",
      Menu.i("Login") / "login" >> RequireNotLoggedIn,
      Menu.i("Password Reset") / "password-reset",
      Menu.i("Lost Password") / "lost-password",
      Menu.i("Cart") / "cart",
      Menu(Loc("logout", List("logout"), S.?("menu.logout"),
        EarlyResponse(() => { User.logUserOut; S.redirectTo(S.referer openOr "/") }),
        If(User.isLoggedIn _, S.?("logout.error"))
      )),      
      // more complex because this menu allows anything in the static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), "Static Content")),
      Menu.i("Admin Items") / "admin" / "item" / "index",
      Menu.i("Admin Users") / "admin" / "users" / "index",   
      Profile.profileParamMenu,
      ViewItem.viewItemParam,
      ViewItem.picsItemParam,    
      Menu("Admin") / "admin" >> Hidden >> RequireLoggedIn,
      Menu.i("Item List") / "item" / "index",
      Menu.i("Item Create") / "item" / "create",
      Menu.i("Item Edit") / "item" / "edit",
      Menu.i("Item Lists") / "item" / "list",
      Menu.i("Item View") / "item" / "view",
      Menu.i("Item Admin") / "item" / "admin",
      //Menu.i("List") / "comments" >> RequireLoggedIn,
      Menu.i("Admin") / "admin" / * >> RequireLoggedIn,// >> loggedIn,
      Menu.i("Error") / "error" >> Hidden,
      Menu.i("404") / "404" >> Hidden,
      Menu.i("403") / "403" >> Hidden,
      Menu.i("Throw") / "throw" >> EarlyResponse(() => throw new Exception("This is only a test."))  
    )


    // set the default htmlProperties
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    // Build SiteMap
    //LiftRules.setSiteMap(Site.siteMap)

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery191
    JQueryModule.init()

    //by santo
    LiftRules.resourceNames = "SANTIX" :: Nil

    //by santo
    ResourceServer.allow {
      case "assets" :: _ => true //assets, in main/resources/toserve/
    }

    //by santo, redirect
    //http://groups.google.com/group/liftweb/browse_thread/thread/b7c071c5c8e3ec75
    LiftRules.uriNotFound.prepend(NamedPF("404Handler"){
      case (req,failure) =>
        NotFoundAsTemplate(ParsePath(List("404"), "html" ,false, false))
    })

    //by santo
    LiftRules.responseTransformers.append {
      case r if r.toResponse.code == 403 => RedirectResponse("403")
      case r => r
    }

  }


}