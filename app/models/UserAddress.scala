package models

case class UserAddress(id: Long, userId: Long, firstname: String, lastname: String, address: String,
                       zipcode: String, city: String, country: String)
