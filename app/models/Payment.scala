package models

import play.api.libs.json.Json

case class Payment(id: Long, userId: Long, creditCardId: Long, amount: Int)

object Payment {
  implicit val paymentFormat = Json.format[Payment]
}
