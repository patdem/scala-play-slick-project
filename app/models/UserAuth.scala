package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.{Json, OFormat}

case class UserAuth(id: Long, loginInfo: LoginInfo, email: String) extends Identity

object UserAuth {
  implicit val loginInfoFormat: OFormat[LoginInfo] = Json.format[LoginInfo]
  implicit val userFormat: OFormat[UserAuth] = Json.format[UserAuth]
}