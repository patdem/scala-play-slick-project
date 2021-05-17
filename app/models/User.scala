package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Long, email: String, nickname: String, password: String)

object User {
  implicit val userInfoFormat: OFormat[User] = Json.format[User]
}

case class CreateUser(email: String, nickname: String, password: String)
object CreateUser {
  implicit val userFormat: OFormat[CreateUser] = Json.format[CreateUser]
}