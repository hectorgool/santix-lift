package code
package lib


import net.liftweb._
import http._
import util.Helpers._
import scala.xml.{NodeSeq,Text}
import util._
import http.js.JsCmds._
import http.js._
import http.js.JE._
import common._
import scala.xml._
import model.User
//import org.bson.types.ObjectId
import http.{Factory}


trait UserHelper extends Factory with Loggable {

  //beta
  val logRounds = new FactoryMaker[Int](10) {}
  
  def ajaxLiveText( value: String, func: String => JsCmd, attrs: (String, String)* ): Elem = {
    S.fmapFunc(S.SFuncHolder(func)) {
      funcName =>
        (attrs.foldLeft(<input />)(_ % _)) %
          ("onkeyup" -> SHtml.makeAjaxCall( JsRaw("'" + funcName + "=' + " + "encodeURIComponent(this.value)") ) )
    }
  }

  def isMatch(toTest: String, encryptedPassword: String): Boolean = {
    if (toTest.length > 0 && encryptedPassword.length > 0)
      tryo(BCrypt.checkpw(toTest, encryptedPassword)).openOr(false)
    else
      false
  }

  def emptyToZero( in: String ): Double = {
    if (!in.isEmpty){
      in.toDouble
    }
    else{
      0.0
    }
  }


}

