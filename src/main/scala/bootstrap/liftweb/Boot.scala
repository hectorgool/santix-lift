package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._

import code.lib._
import code.model._
import code.snippet._
import code.api.UserRest

import net.liftmodules.mongoauth.MongoAuth
import net.liftmodules.mongoauth.Locs

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable with Locs{


  def boot {

    //Could not initialize class net.liftmodules.mongoauth.model.ExtSession$
    MongoAuth.authUserMeta.default.set(User)

    //by santo
    MongoConfig.init

    //by santo
    SmtpMailer.init

    //by santo
    User.createSystemUser

    //by santo
    //CustomResourceId.init()

    // where to search snippet
    LiftRules.addToPackages("code")

    //santo
    // For S.loggedIn_? and TestCond.loggedIn/Out builtin snippet
    LiftRules.loggedInTest = Full(() => User.isLoggedIn)

    // checks for ExtSession cookie    
    LiftRules.earlyInStateful.append(User.testForExtSession)

    //beta
    UserRest.init()

    // Build SiteMap
    val entries = List(
      Menu.i("Home") /("index"), // the simple way to declare a menu
      Menu.i("Signup") / "signup" >> RequireNotLoggedIn,
      Menu.i("Login") / "login" >> RequireNotLoggedIn,
      Menu.i("Password Reset") / "password-reset" >> RequireNotLoggedIn,
      Menu.i("Lost Password") / "lost-password" >> RequireNotLoggedIn,
      Menu.i("Cart") / "cart",
      Menu(Loc("logout", List("logout"), S.?("menu.logout"),
        EarlyResponse(() => { User.logUserOut; S.redirectTo(S.referer openOr "/") }),
        If(User.isLoggedIn _, S.?("logout.error"))
      )),      
      // more complex because this menu allows anything in the /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), "Static Content")),
      Menu("Admin") / "admin" >> Hidden >> RequireLoggedIn,
      Menu.i("Admin") / "admin" / * >> RequireLoggedIn,
      Menu.i("Admin Items") / "admin" / "item" / "index" >> RequireLoggedIn,
      Menu.i("Admin Users") / "admin" / "users" / "index" >> RequireLoggedIn,
      Menu.i("Item List") / "item" / "index",
      Menu.i("Item View") / "item" / "view",
      Menu.i("Item Lists") / "item" / "list",      
      Menu.i("Item Create") / "item" / "create" >> RequireLoggedIn,
      Menu.i("Item Edit") / "item" / "edit" >> RequireLoggedIn,      
      Menu.i("Item Delete") / "item" / "delete" >> RequireLoggedIn,
      Menu.i("Item Admin") / "item" / "admin" >> RequireLoggedIn,
      Menu.i("Error") / "error" >> Hidden,
      Menu.i("404") / "404" >> Hidden,
      Menu.i("403") / "403" >> Hidden,
      Menu.i("Throw") / "throw" >> Hidden >> EarlyResponse(() => throw new Exception("This is only a test.")),
      Profile.profileParamMenu,
      Profile.catalogParamMenu,
      ViewItem.viewItemParam,
      ViewItem.picsItemParam,
      ViewUserItem.menu,
      ViewUserItem.menuCatalogItem,
      ViewUserItem.menuCatalogItemPics
    )

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

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

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
    //beta
    def my403 : Box[LiftResponse] =
      for {
        session <- S.session
        req <- S.request
        template = Templates("403" :: Nil)
        response <- session.processTemplate(template, req, req.path, 403)
      } yield response

    LiftRules.responseTransformers.append {
      case resp if resp.toResponse.code == 403 => my403 openOr resp
      case resp => resp
    }

    //beta
    //by santo
    /*   
    LiftRules.exceptionHandler.prepend {
      case (runMode, request, exception) =>
        logger.error("Boom! At "+request.uri)
        InternalServerErrorResponse()
    }
    */
    
    //You want to ensure clients are using HTTPS.
    /*
    LiftRules.earlyResponse.append { (req: Req) =>
      //Amazon Load Balancer
      //if (req.header("X-Forwarded-Proto") != Full("https") ) {
      if (req.request.scheme != "https") {
        val uriAndQuery = req.uri +
        (req.request.queryString.map(s => "?"+s) openOr "")
        val uri = "https://%s%s".format(req.request.serverName, uriAndQuery)
        Full(PermRedirectResponse(uri, req, req.cookies: _*))
      }
      else Empty
    } 
    */   

    //beta    


  }


}
