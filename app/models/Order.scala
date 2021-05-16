package models

import play.api.libs.json.Json

case class Order(id: Long, userId: Long, paymentId: Long, voucherId: Long, promoCodeId: Long)

object Order {
  implicit val orderFormat = Json.format[Order]
}
