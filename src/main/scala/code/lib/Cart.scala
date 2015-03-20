package code
package lib


import model._
import net.liftweb._
import util._
import net.liftweb.json._
import net.liftweb.http.js.{JsCmd, JsCmds}
import common.Logger
import scala.math.BigDecimal.double2bigDecimal


class Cart extends Logger{

  /**
   * The contents of the cart
   */
  val contents = ValueCell[ Vector[ CartItem ] ]( Vector() )

  /**
   * The subtotal
   */
  val subtotal = contents.lift( _.foldLeft(zero)( _ + _.quantityMultiply( _.price.get ) ) )

  /**
   * The taxable subtotal
   */
  // val taxableSubtotal = contents.lift( _.filter(_.taxable). foldLeft(zero)( _ + _.quantityMultiply( _.price ) ) ) 
  val taxableSubtotal = contents.lift( _.foldLeft(zero)( _ + _.quantityMultiply( _.price.get ) ) )

  /**
   * The current tax rate
   */
  //val taxRate = ValueCell(BigDecimal("0.07"))
  val taxRate = ValueCell(0.07d)

  /**
   * The computed tax
   */
  val tax = taxableSubtotal.lift(taxRate)(_ * _)

  /**
   * The total
   */
  val total = subtotal.lift(tax)(_ + _)

  /**
   * The weight of the cart
   */
  val weight = contents.lift(_.foldLeft(zero)(_ +_.quantityMultiply(_.weight.get )) )  

  // Helper methods

  /**
   * A nice constant zero
   */
  //def zero = BigDecimal(0)
  def zero = 0d

  /**
   * Add an item to the cart.  If it's already in the cart,
   * then increment the quantity
   */
  //beta
  def addToCart(item: Items) {

    println("\n addToCart, item: " + item + "\n")//depurar

    println("\n contents: " + contents + "\n")//depurar

    
    contents.atomicUpdate( v => v.find( _.item == item ) match {
      case Some(ci) => 
        v.map(ci => ci.copy(qnty = ci.qnty + 
        ( 
          if (ci.item == item)
           1 
          else
           0
        )))
      case _ => 
        v :+ CartItem(item, 1)
    })
    
    
  }

  /**
   * Set the item quantity.  If zero or negative, remove
   */
  def setItemCnt(item: Items, qnty: Int) {
    if (qnty <= 0) removeItem(item)
    else contents.atomicUpdate(v => v.find(_.item == item) match {
      case Some(ci) => v.map(ci => ci.copy(qnty =
        (if (ci.item == item) 
          qnty 
        else 
          ci.qnty)))
      case _ => v :+ CartItem(item, qnty)
    })

  }

  /**
   * Removes an item from the cart
   */
  def removeItem(item: Items) {
    contents.atomicUpdate(_.filterNot(_.item == item))
  }


}

/**
 * An item in the cart
 */
case class CartItem( item: Items, qnty: Int, id: String = Helpers.nextFuncName ) {

  /**
   * Multiply the quantity times some calculation on the
   * contained Item (e.g., getting its weight)
   */
  def quantityMultiply( f: Items => Double ): Double = {
    f( item ) * qnty
  }


}

/**
 * The CartItem companion object
 */
object CartItem {


  implicit def cartItemToItem(in: CartItem): Items = in.item


}
