package models

case class CreditCard(id: Long, userId: Long, cardName: String, cardNumber: Long, expDate: String, cvcCode: String)
