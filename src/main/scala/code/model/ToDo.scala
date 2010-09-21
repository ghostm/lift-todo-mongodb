package code.model 
import scala.List
import net.liftweb.util.FieldError
import net.liftweb.record.field._
import net.liftweb.common._
import net.liftweb.json.JsonAST._
import com.mongodb._
import com.mongodb.util.JSON
import org.bson.types.ObjectId
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.mongodb._
import net.liftweb.json.JsonDSL._


                  
class ToDo private () extends MongoRecord[ToDo] with MongoId[ToDo]{
 def meta = ToDo
 
 object done extends BooleanField(this)
 object owner extends LongField(this)
 object priority extends IntField(this) {
   override def defaultValue = 5
   override def validations = validPriority _ :: super.validations

   def validPriority(in: Int): List[FieldError] =
    if (in > 0 && in <= 10) Nil
    else List(FieldError(this, <b>Priority must be 1-10</b>))
 } 
 object desc extends StringField(this, 128) {
   override def validations =
    validLength _ ::
      super.validations
   def validLength(in: String): List[FieldError] = {
    if (in.size >= 3) Nil
    else List(FieldError(this, <b>Description must be at least 3 characters</b>))
   }

 }
}
 
object ToDo extends ToDo with MongoMetaRecord[ToDo]{
  lazy val priorityList = (1 to 10).map(v => (v.toString, v.toString))

  def findAll(tempOwnerBox:Box[User], excludeDone:Boolean):List[ToDo] = {
    val tempUser = tempOwnerBox.open_!
    val tempReturn = ToDo.findAll(("owner" -> tempUser.id.is))
    if(excludeDone)
    	return tempReturn.filter(_.done.value == false)
	tempReturn
  }
}