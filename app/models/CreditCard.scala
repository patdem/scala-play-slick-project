package models

import play.api.libs.json.{Json, OFormat}

case class CreditCard(id: Long, userId: Long, cardName: String, cardNumber: String, expDate: String, cvcCode: String)

object CreditCard {
  implicit val creditCardFormat: OFormat[CreditCard] = Json.format[CreditCard]
}

case class CreateCreditCard(userId: Long, cardName: String, cardNumber: String, expDate: String, cvcCode: String)
object CreateCreditCard {
  implicit val creditCardFormat: OFormat[CreateCreditCard] = Json.format[CreateCreditCard]
}