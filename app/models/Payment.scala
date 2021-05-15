package models

case class Payment(id: Long, userId: Long, creditCardId: Long, amount: Int)
