package models

case class Order(id: Long, userId: Long, paymentId: Long, voucherId: Long, promoCode: Long)
