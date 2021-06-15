package services

import models.Voucher
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VoucherRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class VoucherTable(tag: Tag) extends Table[Voucher](tag, "voucher") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def amount = column[Int]("amount")


    override def * = (id, amount) <> ((Voucher.apply _).tupled, Voucher.unapply)
  }

  val voucherTable = TableQuery[VoucherTable]

  def create(amount: Int): Future[Voucher] =db.run {
    (voucherTable.map(v => (v.amount))
      returning voucherTable.map(_.id)
      into ((amount, id) => Voucher(id, amount))
      ) += (amount)
  }

  def getById(id: Long): Future[Option[Voucher]] = db.run {
    voucherTable.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Voucher]] = db.run {
    voucherTable.result
  }

  def update(id: Long, newVoucher: Voucher): Future[Int] = db.run {
    val newUpdate: Voucher = newVoucher.copy(id)
    voucherTable.filter(_.id === id).update(newUpdate)
  }

  def delete(id: Long): Future[Int] = db.run {
    voucherTable.filter(_.id === id).delete
  }

}
