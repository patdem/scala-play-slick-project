package models

import play.api.libs.json.Json

case class Voucher(id: Long, amount: Int)

object Voucher {
  implicit val voucherFormat = Json.format[Voucher]
}
