package code
package snippet


import net.liftweb.http.NamedCometActorSnippet


object AddResultComet extends NamedCometActorSnippet {


  def name = "result"
  def cometClass = "ElasticSearchComet"


}