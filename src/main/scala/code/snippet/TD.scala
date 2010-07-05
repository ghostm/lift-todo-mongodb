package code.snippet 

import code._
import net.liftweb.common.Full
import model._ 

import net.liftweb._ 
import http._ 

import SHtml._ 
import S._ 

import js._ 
import JsCmds._ 

import mapper._ 

import util._ 
import Helpers._ 

import scala.xml.{NodeSeq, Text}

object QueryNotDone extends SessionVar(false) 

class TD { 
  def add(form: NodeSeq) = {
    val tempUser = User.currentUser.open_!
    var todo = ToDo.createRecord
    var newDesc = todo.desc.toString
    var newPriorityString = todo.priority.toString

    def checkAndSave(): Unit = {
      todo.desc.set(newDesc)
      todo.priority.set(newPriorityString.toInt)
      todo.owner.set(tempUser.id.toString)
      println(todo+" "+todo.priority.value+" "+todo.desc.value)
      todo.validate match {
        case Nil => todo.save ; S.notice("Added "+todo.desc)
        case xs => S.error(xs) ; S.mapSnippet("TD.add", doBind)
      }
    }
      
    def doBind(form: NodeSeq) = 
    bind("todo", form, 
      "priority" -> SHtml.text(newPriorityString, newPriorityString = _),
      "desc" -> SHtml.text(newDesc, newDesc = _),
      "submit" -> submit("New", checkAndSave)) 
      
    doBind(form) 
  } 
  
  def list(html: NodeSeq) = { 
    val id = S.attr("all_id").open_! 
    def inner(): NodeSeq = { 
      def reDraw() = SetHtml(id, inner()) 
      
      bind("todo", html, 
        "exclude" -> 
        ajaxCheckbox(QueryNotDone, v => {QueryNotDone(v); reDraw}), 
        "list" -> doList(reDraw) _) 
    } 
    
    inner() 
  } 
  
  /*
  private def toShow = 
    ToDo.findAll(By(ToDo.owner, User.currentUser), 
      if (QueryNotDone) By(ToDo.done, false) 
      else Ignore[ToDo], 
      OrderBy(ToDo.done, Ascending), 
      OrderBy(ToDo.priority, Descending), 
      OrderBy(ToDo.desc, Ascending))
      */
  //private def toShow = ToDo.findAll()
  
  private def desc(td: ToDo, reDraw: () => JsCmd) = 
    swappable(<span>{td.desc.toString}</span>,
      <span>{ajaxText(td.desc.toString, 
        v => {td.desc.set(v); td.save; reDraw()})}
      </span>) 
  
  private def doList(reDraw: () => JsCmd)(html: NodeSeq): NodeSeq = 
    ToDo.findAll(User.currentUser, QueryNotDone).
    flatMap(td => 
      bind("todo", html, 
        "check" -> ajaxCheckbox(td.done.value,
          v => {td.done.set(v); td.save; reDraw()}),
        "priority" ->
          ajaxSelect(ToDo.priorityList, Full(td.priority.toString),
          v => {td.priority.set(v.toInt); td.save; reDraw()}),
        "desc" -> desc(td, reDraw)
      )) 
} 
