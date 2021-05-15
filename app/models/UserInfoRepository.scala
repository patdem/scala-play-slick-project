package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserInfoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserInfoTable(tag: Tag) extends Table[UserInfo](tag, "userInfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def nickname = column[String]("nickname")
    def password = column[String]("password")


    override def * = (id, email, nickname, password) <> ((UserInfo.apply _).tupled, UserInfo.unapply)
  }

  val userInfoTable = TableQuery[UserInfoTable]

  def create(email: String, nickname: String, password: String): Future[UserInfo] =db.run {
    (userInfoTable.map(u => (u.email, u.nickname, u.password))
      returning userInfoTable.map(_.id)
      into { case ((email, nickname, password), id) => UserInfo(id, email, nickname, password) }
      ) += (email, nickname, password)
  }

  def getById(id: Long): Future[Option[UserInfo]] = db.run {
    userInfoTable.filter(_.id === id).result.headOption
  }

  def getByName(nickname: String): Future[Option[UserInfo]] = db.run {
    userInfoTable.filter(_.nickname === nickname).result.headOption
  }

  def list(): Future[Seq[UserInfo]] = db.run {
    userInfoTable.result
  }

  def update(id: Long, new_userInfo: UserInfo): Future[Int] = db.run {
    val new_user: UserInfo = new_userInfo.copy(id)
    userInfoTable.filter(_.id === id).update(new_user)
  }

  def delete(id: Long): Future[Int] = db.run {
    userInfoTable.filter(_.id === id).delete
  }

}
