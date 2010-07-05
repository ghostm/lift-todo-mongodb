/*
 * Copyright 2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package code {
package db {

import net.liftweb.couchdb._
import dispatch.{Http, StatusCode}
import net.liftweb.common.{Failure, Full}
import net.liftweb.json.Implicits.{int2jvalue, string2jvalue}
import net.liftweb.json.JsonAST.{JField, JInt, JObject, JString, render}
import net.liftweb.json.JsonDSL.{jobject2assoc, pair2Assoc, pair2jvalue}


object TodoDB {
  import CouchDB.defaultDatabase

  val design: JObject =
    ("language" -> "javascript") ~
    ("views" -> (("todo_findAll" ->  ("map" -> "function(doc) { if (doc.type == 'ToDo'){emit(doc.owner, doc)};}"))))

  def setup = {
    val database = new Database("todo_couchdb")
    try { Http(database info) } catch {
      case StatusCode(404, _) => {
              Http(database create)
              Http(database.design("todo_couchdb") put design)
      }
    }
    defaultDatabase = database
  }
}

}
}
