package code
package snippet


import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml, Templates}
import net.liftweb.http.js.JsCmds.{SetHtml, Noop}
import net.liftweb.http.js.JsCmd


object TemplatesLoader {


	def panelContent : JsCmd = {
   	Templates( "templates-hidden" :: "admin" :: "index" :: Nil ).map(ns => SetHtml("admin-content", ns)) openOr Noop
  }

	def itemsContent : JsCmd = {
   	Templates( "templates-hidden" :: "admin" :: "items" :: Nil ).map(ns => SetHtml("admin-content", ns)) openOr Noop
  }
    
	def usersContent : JsCmd = {
   	Templates( "templates-hidden" :: "admin" :: "users" :: Nil ).map(ns => SetHtml("admin-content", ns)) openOr Noop
  }
   
  def cartContent : JsCmd = {
    Templates( "templates-hidden" :: "cart" :: Nil ).map(ns => SetHtml("contenido", ns)) openOr Noop
  }

  def morePics : JsCmd = {
    Templates( "templates-hidden" :: "items" ::"more-pics" :: Nil ).map(ns => SetHtml("contenido", ns)) openOr Noop
  }


	def render = {

		"#panel [onclick]" #> SHtml.ajaxInvoke( panelContent _) &
		"#items [onclick]" #> SHtml.ajaxInvoke( itemsContent _) &
		"#users [onclick]" #> SHtml.ajaxInvoke( usersContent _)
		
	}

  def cart = {

    "#cart [onclick]" #> SHtml.ajaxInvoke( cartContent _)

  }

  def morePicsButton = {

    "#morepics [onclick]" #> SHtml.ajaxInvoke( morePics _)

  }

}