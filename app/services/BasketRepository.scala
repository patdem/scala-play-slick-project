package services

import models.Basket
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BasketRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                 val userInfoRepository: UserRepository,
                                 val productRepository: ProductRepository)
                               (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class BasketTable(tag: Tag) extends Table[Basket](tag, "basket") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("userId")
    def userId_fk = foreignKey("userId_fk", userId, userInfo_)(_.id)

    def productId = column[Long]("productId")
    def productId_fk = foreignKey("productId_fk", productId, product_)(_.id)

    override def * = (id, userId, productId) <> ((Basket.apply _).tupled, Basket.unapply)
  }

  import productRepository.ProductTable
  import userInfoRepository.UserInfoTable

  val basketTable = TableQuery[BasketTable]
  val userInfo_ = TableQuery[UserInfoTable]
  val product_ = TableQuery[ProductTable]

  def create(userId: Long, productId: Long): Future[Basket] = db.run {
    (basketTable.map(b => (b.userId, b.productId))
      returning basketTable.map(_.id)
      into { case ((userId, productId), id) =>
      Basket(id, userId, productId) }
      ) += (userId,productId)
  }

  def getById(id: Long): Future[Option[Basket]] = db.run {
    basketTable.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[Basket]] = db.run {
    basketTable.result
  }

  def update(id: Long, new_basket: Basket): Future[Int] = db.run {
    val new_update: Basket = new_basket.copy(id)
    basketTable.filter(_.id === id).update(new_update)
  }

  def delete(id: Long): Future[Int] = db.run {
    basketTable.filter(_.id === id).delete
  }

}