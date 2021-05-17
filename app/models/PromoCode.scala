package models

import play.api.libs.json.{Json, OFormat}

case class PromoCode(id: Long, name: String, amount: Int)

object PromoCode {
  implicit val promoCodeFormat: OFormat[PromoCode] = Json.format[PromoCode]
}

case class CreatePromoCode(name: String, amount: Int)
object CreatePromoCode {
  implicit val promoCodeFormat: OFormat[CreatePromoCode] = Json.format[CreatePromoCode]
}