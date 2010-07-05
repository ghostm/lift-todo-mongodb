package code.model 
import scala.List
import net.liftweb.util.FieldError
import net.liftweb.couchdb._ 
import net.liftweb.record.field._
import net.liftweb.common._
import net.liftweb.json.JsonAST.{JField, JInt, JObject, JString, render}
                  
class ToDo extends CouchRecord[ToDo]{
 def meta = ToDo
 
 object done extends BooleanField(this)
 object owner extends StringField(this, 64)
 object priority extends IntField(this) {
   override def defaultValue = 5
   override def validators = validPriority _ :: super.validators

   def validPriority(in: Box[Int]): List[FieldError] =
    if (in.open_! > 0 && in.open_! <= 10) Nil
    else List(FieldError(this, <b>Priority must be 1-10</b>))
 } 
 object desc extends StringField(this, 128) {
   override def validators =
    validLength _ ::
      super.validators
   def validLength(in: Box[String]): List[FieldError] = {
    val inString = in.open_!
    if (inString.size >= 3) Nil
    else List(FieldError(this, <b>Description must be at least 3 characters</b>))
   }

 }
}
 
object ToDo extends ToDo with CouchMetaRecord[ToDo]{
  def createRecord = new ToDo
  lazy val priorityList = (1 to 10).map(v => (v.toString, v.toString))

  def findAll(tempOwnerBox:Box[User], excludeDone:Boolean):List[ToDo] = {
    val tempUser = tempOwnerBox.open_!
    val viewReturn = ToDo.queryView("todo_couchdb", "todo_findAll", _.key(JString(tempUser.id.toString)))
    viewReturn match {
      case Full(v) => {
        if(excludeDone){
          return v.toList.filter(_.done.value == false)
        }
        return v.toList
      }
      case Empty => return Nil
    }
  }
}