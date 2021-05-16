package services

import models.Payment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val userInfoRepository: UserRepository,
                                   val creditCardRepository: CreditCardRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("userId")
    def userId_fk = foreignKey("userId_fk", userId, userInfo_)(_.id)

    def creditCardId = column[Long]("creditCardId")
    def creditCardId_fk =
      foreignKey("creditCardId_fk", creditCardId, creditCard_)(_.id)

    def amount = column[Int]("amount")

    override def * = (id, userId, creditCardId, amount) <>
      ((Payment.apply _).tupled, Payment.unapply)
  }

  import creditCardRepository.CreditCardTable
  import userInfoRepository.UserInfoTable

  val paymentTable = TableQuery[PaymentTable]
  val userInfo_ = TableQuery[UserInfoTable]
  val creditCard_ = TableQuery[CreditCardTable]

  def create(userId: Long, creditCardId: Long, amount: Int): Future[Payment] = db.run {
    (paymentTable.map(p => (p.userId, p.creditCardId, p.amount))
      returning paymentTable.map(_.id)
      into { case ((userId, creditCardId, amount), id) =>
      Payment(id, userId, creditCardId, amount) }
      ) += (userId, creditCardId, amount)
  }

  def getById(id: Long): Future[Option[Payment]] = db.run {
    paymentTable.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Payment]] = db.run {
    paymentTable.result
  }

  def update(id: Long, new_payment: Payment): Future[Int] = db.run {
    val new_update: Payment = new_payment.copy(id)
    paymentTable.filter(_.id === id).update(new_update)
  }

  def delete(id: Long): Future[Int] = db.run {
    paymentTable.filter(_.id === id).delete
  }

}
