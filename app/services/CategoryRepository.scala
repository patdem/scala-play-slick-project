package services

import models.Category
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")


    override def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  val categoryTable = TableQuery[CategoryTable]

  def create(name: String): Future[Category] =db.run {
    (categoryTable.map(c => (c.name))
      returning categoryTable.map(_.id)
      into ((name, id) => Category(id, name))
      ) += (name)
  }

  def getById(id: Long): Future[Option[Category]] = db.run {
    categoryTable.filter(_.id === id).result.headOption
  }

  def getByName(name: String): Future[Option[Category]] = db.run {
    categoryTable.filter(_.name === name).result.headOption
  }

  def list(): Future[Seq[Category]] = db.run {
    categoryTable.result
  }

  def update(id:Long, newCategory: Category): Future[Int] = {
    val categoryToUpdate: Category = newCategory.copy(id)
    db.run(categoryTable.filter(_.id === id).update(categoryToUpdate))
  }

  def delete(id: Long): Future[Int] = db.run {
    categoryTable.filter(_.id === id).delete
  }

}
