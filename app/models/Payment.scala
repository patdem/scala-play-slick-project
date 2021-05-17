package models

import play.api.libs.json.{Json, OFormat}

case class Payment(id: Long, userId: Long, creditCardId: Long, amount: Int)

object Payment {
  implicit val paymentFormat: OFormat[Payment] = Json.format[Payment]
}

case class CreatePayment(userId: Long, creditCardId: Long, amount: Int)
object CreatePayment {
  implicit val paymentFormat: OFormat[CreatePayment] = Json.format[CreatePayment]
}