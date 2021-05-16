package models

import play.api.libs.json.Json

case class Product(id: Long, name: String, description: String, category: Long)

object Product {
  implicit val productFormat = Json.format[Product]
}
