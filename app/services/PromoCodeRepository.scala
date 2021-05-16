package services

import models.PromoCode
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PromoCodeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PromoCodeTable(tag: Tag) extends Table[PromoCode](tag, "promoCode") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def amount = column[Int]("amount")


    override def * = (id, name, amount) <> ((PromoCode.apply _).tupled, PromoCode.unapply)
  }

  val promoCodeTable = TableQuery[PromoCodeTable]

  def create(name: String, amount: Int): Future[PromoCode] =db.run {
    (promoCodeTable.map(p => (p.name, p.amount))
      returning promoCodeTable.map(_.id)
      into { case ((name, amount), id) => PromoCode(id, name, amount) }
      ) += (name, amount)
  }

  def getById(id: Long): Future[Option[PromoCode]] = db.run {
    promoCodeTable.filter(_.id === id).result.headOption
  }

  def getByName(name: String): Future[Option[PromoCode]] = db.run {
    promoCodeTable.filter(_.name === name).result.headOption
  }

  def list(): Future[Seq[PromoCode]] = db.run {
    promoCodeTable.result
  }

  def update(id: Long, new_category: PromoCode): Future[Int] = db.run {
    val new_update: PromoCode = new_category.copy(id)
    promoCodeTable.filter(_.id === id).update(new_update)
  }

  def delete(id: Long): Future[Int] = db.run {
    promoCodeTable.filter(_.id === id).delete
  }

}
