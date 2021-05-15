package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val categoryRepository: CategoryRepository)
                                  (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")

    def category = column[Long]("category")
    def category_fk = foreignKey("category_fk", category, category_)(_.id)

    override def * = (id, name, description, category) <> ((Product.apply _).tupled, Product.unapply)
  }

  import categoryRepository.CategoryTable

  val productTable = TableQuery[ProductTable]
  val category_ = TableQuery[CategoryTable]

  def create(name: String, description: String, categoryId: Long): Future[Product] = db.run {
    (productTable.map(p => (p.name, p.description, p.category))
      returning productTable.map(_.id)
      into { case ((name, description, categoryId), id) => Product(id, name, description, categoryId) }
      ) += (name, description, categoryId)
  }

  def getById(id: Long): Future[Option[Product]] = db.run {
    productTable.filter(_.id === id).result.headOption
  }

  def getByName(name: String): Future[Option[Product]] = db.run {
    productTable.filter(_.name === name).result.headOption
  }

  def list(): Future[Seq[Product]] = db.run {
    productTable.result
  }

  def update(id: Long, new_name: String): Future[Int] = db.run {
    productTable.filter(_.id === id).map(_.name).update(new_name)
  }

  def delete(id: Long): Future[Int] = db.run {
    productTable.filter(_.id === id).delete
  }

}
