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

import code.config._

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {


  def boot {

    // init mongodb
    MongoConfig.init()

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

    // set the default htmlProperties
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    // Build SiteMap
    LiftRules.setSiteMap(Site.siteMap)

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    //LiftRules.setSiteMap(SiteMap(entries:_*))

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
      case r if r.toResponse.code == 403 => RedirectResponse("/403.html")
      case r => r
    }

  }


}