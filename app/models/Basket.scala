package models

import play.api.libs.json.{Json, OFormat}

case class Basket(id: Long, userId: Long, productId: Long)

object Basket {
  implicit val basketFormat: OFormat[Basket] = Json.format[Basket]
}

case class CreateBasket(userId: Long, productId: Long)
object CreateBasket {
  implicit val basketFormat: OFormat[CreateBasket] = Json.format[CreateBasket]
}