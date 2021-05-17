package models

import play.api.libs.json.{Json, OFormat}

case class Order(id: Long, userId: Long, paymentId: Long, voucherId: Long, promoCodeId: Long)

object Order {
  implicit val orderFormat: OFormat[Order] = Json.format[Order]
}

case class CreateOrder(userId: Long, paymentId: Long, voucherId: Long, promoCodeId: Long)
object CreateOrder {
  implicit val orderFormat: OFormat[CreateOrder] = Json.format[CreateOrder]
}