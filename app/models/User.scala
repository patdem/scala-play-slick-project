package models

import play.api.libs.json.Json

case class User(id: Long, email: String, nickname: String, password: String)

object User {
  implicit val userInfoFormat = Json.format[User]
}