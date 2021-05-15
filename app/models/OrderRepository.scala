package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                      val userInfoRepository: UserInfoRepository,
                                      val paymentRepository: PaymentRepository,
                                      val voucherRepository: VoucherRepository,
                                      val promoCodeRepository: PromoCodeRepository)
                                     (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("userId")
    def userId_fk = foreignKey("userId_fk", userId, userInfo_)(_.id)

    def paymentId = column[Long]("paymentId")
    def paymentId_fk = foreignKey("paymentId_fk", paymentId, payment_)(_.id)

    def voucherId = column[Long]("voucherId")
    def voucherId_fk = foreignKey("voucherId_fk", voucherId, voucher_)(_.id)

    def promoCodeId = column[Long]("promoCodeId")
    def promoCodeId_fk = foreignKey("voucherId_fk", promoCodeId, promoCode_)(_.id)

    override def * = (id, userId, paymentId, voucherId, promoCodeId) <> ((Order.apply _).tupled, Order.unapply)
  }

  import userInfoRepository.UserInfoTable
  import paymentRepository.PaymentTable
  import voucherRepository.VoucherTable
  import promoCodeRepository.PromoCodeTable

  val orderTable = TableQuery[OrderTable]
  val userInfo_ = TableQuery[UserInfoTable]
  val payment_ = TableQuery[PaymentTable]
  val voucher_ = TableQuery[VoucherTable]
  val promoCode_ = TableQuery[PromoCodeTable]

  def create(userId: Long, paymentId: Long, voucherId: Long, promoCodeId: Long): Future[Order] = db.run {
    (orderTable.map(o => (o.userId, o.paymentId, o.voucherId, o.promoCodeId))
      returning orderTable.map(_.id)
      into { case ((userId, paymentId, voucherId, promoCodeId), id) =>
      Order(id, userId, paymentId, voucherId, promoCodeId) }
      ) += (userId, paymentId, voucherId, promoCodeId)
  }

  def getById(id: Long): Future[Option[Order]] = db.run {
    orderTable.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Order]] = db.run {
    orderTable.result
  }

  def update(id: Long, new_order: Order): Future[Int] = db.run {
    val new_update: Order = new_order.copy(id)
    orderTable.filter(_.id === id).update(new_update)
  }

  def delete(id: Long): Future[Int] = db.run {
    orderTable.filter(_.id === id).delete
  }

}