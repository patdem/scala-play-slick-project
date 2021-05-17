package models

import play.api.libs.json.{Json, OFormat}

case class Category(id: Long, name: String)


object Category {
  implicit val categoryFormat: OFormat[Category] = Json.format[Category]
}

case class CreateCategory(name: String)
object CreateCategory {
  implicit val categoryFormat: OFormat[CreateCategory] = Json.format[CreateCategory]
}