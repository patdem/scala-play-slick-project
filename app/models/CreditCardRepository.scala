package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreditCardRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val userInfoRepository: UserInfoRepository)
                                  (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CreditCardTable(tag: Tag) extends Table[CreditCard](tag, "credit_card") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("userId")
    def userId_fk = foreignKey("userId_fk", userId, userInfo_)(_.id)

    def cardName = column[String]("cardName")
    def cardNumber = column[Long]("cardNumber")
    def expDate = column[String]("expDate")
    def cvcCode = column[String]("cvcCode")

    override def * = (id, userId, cardName, cardNumber, expDate, cvcCode) <>
      ((CreditCard.apply _).tupled, CreditCard.unapply)
  }

  import userInfoRepository.UserInfoTable

  val creditCardTable = TableQuery[CreditCardTable]
  val userInfo_ = TableQuery[UserInfoTable]

  def create(userId: Long, cardName: String, cardNumber: Long, expDate: String, cvcCode: String): Future[CreditCard]
  = db.run {
    (creditCardTable.map(c => (c.userId, c.cardName, c.cardNumber, c.expDate, c.cvcCode))
      returning creditCardTable.map(_.id)
      into { case ((userId, cardName, cardNumber, expDate, cvcCode), id) =>
      CreditCard(id, userId, cardName, cardNumber, expDate, cvcCode) }
      ) += (userId, cardName, cardNumber, expDate, cvcCode)
  }

  def getById(id: Long): Future[Option[CreditCard]] = db.run {
    creditCardTable.filter(_.id === id).result.headOption
  }

  def getByName(cardName: String): Future[Option[CreditCard]] = db.run {
    creditCardTable.filter(_.cardName === cardName).result.headOption
  }

  def list(): Future[Seq[CreditCard]] = db.run {
    creditCardTable.result
  }

  def update(id: Long, new_credit_card: CreditCard): Future[Int] = db.run {
    val new_card: CreditCard = new_credit_card.copy(id)
    creditCardTable.filter(_.id === id).update(new_card)
  }

  def delete(id: Long): Future[Int] = db.run {
    creditCardTable.filter(_.id === id).delete
  }

}
