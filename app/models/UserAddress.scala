package models

import play.api.libs.json.{Json, OFormat}

case class UserAddress(id: Long, userId: Long, firstname: String, lastname: String, address: String,
                       zipcode: String, city: String, country: String)

object UserAddress {
  implicit val userAddressFormat: OFormat[UserAddress] = Json.format[UserAddress]
}

case class CreateUserAddress(userId: Long, firstname: String, lastname: String, address: String,
                             zipcode: String, city: String, country: String)
object CreateUserAddress {
  implicit val userAddressFormat: OFormat[CreateUserAddress] = Json.format[CreateUserAddress]
}