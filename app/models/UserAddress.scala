package models

import play.api.libs.json.Json

case class UserAddress(id: Long, userId: Long, firstname: String, lastname: String, address: String,
                       zipcode: String, city: String, country: String)

object UserAddress {
  implicit val userAddressFormat = Json.format[UserAddress]
}