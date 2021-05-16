package models

import play.api.libs.json.Json

case class CreditCard(id: Long, userId: Long, cardName: String, cardNumber: String, expDate: String, cvcCode: String)

object CreditCard {
  implicit val creditCardFormat = Json.format[CreditCard]
}
