package code {
package model {

import _root_.net.liftweb.record.field._
import _root_.net.liftweb.common._

import code.db._
import code.lib._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {  
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)

  // comment this line out to require email validations
  override def skipEmailValidation = true
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def meta = User // what's the "meta" server

  // define an additional field for a personal essay
  object textArea extends StringField(this, 2048) {
    override def displayName = "Personal Essay"
  }
}

}
}
