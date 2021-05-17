package models

import play.api.libs.json.{Json, OFormat}

case class Voucher(id: Long, amount: Int)

object Voucher {
  implicit val voucherFormat: OFormat[Voucher] = Json.format[Voucher]
}

case class CreateVoucher(amount: Int)
object CreateVoucher {
  implicit val voucherFormat: OFormat[CreateVoucher] = Json.format[CreateVoucher]
}