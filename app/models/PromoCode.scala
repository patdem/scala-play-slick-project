package models

import play.api.libs.json.Json

case class PromoCode(id: Long, name: String, amount: Int)

object PromoCode {
  implicit val promoCodeFormat = Json.format[PromoCode]
}