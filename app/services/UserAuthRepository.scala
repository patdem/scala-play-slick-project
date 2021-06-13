package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.UserAuth
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

@Singleton
class UserAuthRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext, implicit val classTag: ClassTag[PasswordInfo]) extends IdentityService[UserAuth] {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  case class UserDto(id: Long, providerId: String, providerKey: String, email: String)

  class UserTable(tag: Tag) extends Table[UserDto](tag, "userAuth") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def providerId = column[String]("providerId")

    def providerKey = column[String]("providerKey")

    def email = column[String]("email")

    def * = (id, providerId, providerKey, email) <> ((UserDto.apply _).tupled, UserDto.unapply)
  }

  val user = TableQuery[UserTable]

  override def retrieve(loginInfo: LoginInfo): Future[Option[UserAuth]] = db.run {
    user.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .result
      .headOption
  }.map(_.map(dto => toModel(dto)))

  def create(providerId: String, providerKey: String, email: String): Future[UserAuth] = db.run {
    (user.map(c => (c.providerId, c.providerKey, c.email))
      returning user.map(_.id)
      into { case ((providerId, providerKey, email), id) => UserDto(id, providerId, providerKey, email) }
      ) += (providerId, providerKey, email)
  }.map(dto => toModel(dto))

  def getAll: Future[Seq[UserAuth]] = db.run {
    user.result
  }.map(_.map(dto => toModel(dto)))

  def getByIdOption(id: Long): Future[Option[UserAuth]] = db.run {
    user.filter(_.id === id).result.headOption
  }.map(_.map(dto => toModel(dto)))

  def getById(id: Long): Future[UserAuth] = db.run {
    user.filter(_.id === id).result.head
  }.map(dto => toModel(dto))

  def update(id: Long, newUser: UserAuth): Future[UserAuth] = {
    val userToUpdate = newUser.copy(id)
    db.run {
      user.filter(_.id === id)
        .update(toDto(userToUpdate))
        .map(_ => userToUpdate)
    }
  }

  def delete(id: Long): Future[Unit] =
    db.run {
      user.filter(_.id === id)
        .delete
        .map(_ => ())
    }

  private def toModel(dto: UserDto): UserAuth =
    UserAuth(dto.id, LoginInfo(dto.providerId, dto.providerKey), dto.email)

  private def toDto(model: UserAuth): UserDto =
    UserDto(model.id, model.loginInfo.providerID, model.loginInfo.providerKey, model.email)
}