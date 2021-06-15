package services

import models.UserAddress
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                      val userInfoRepository: UserRepository)
                                     (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserAddressTable(tag: Tag) extends Table[UserAddress](tag, "userAddress") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def userId = column[Long]("userId")
    def userIdFk = foreignKey("userIdFk", userId, userInfo_)(_.id)

    def firstname = column[String]("firstname")
    def lastname = column[String]("lastname")
    def address = column[String]("address")

    def zipcode = column[String]("zipcode")
    def city = column[String]("city")
    def country = column[String]("country")


    override def * = (id, userId, firstname, lastname, address, zipcode,
      city, country) <> ((UserAddress.apply _).tupled, UserAddress.unapply)
  }

  import userInfoRepository.UserInfoTable

  val userAddressTable = TableQuery[UserAddressTable]
  val userInfo_ = TableQuery[UserInfoTable]

  def create(userId: Long, firstname: String, lastname: String, address: String, zipcode: String, city: String,
             country: String): Future[UserAddress] =db.run {
    (userAddressTable.map(u => (u.userId, u.firstname, u.lastname, u.address, u.zipcode, u.city, u.country))
      returning userAddressTable.map(_.id)
      into { case ((userId, firstname, lastname, address, zipcode, city, country), id) =>
      UserAddress(id, userId, firstname, lastname, address, zipcode, city, country) }
      ) += (userId, firstname, lastname, address, zipcode, city, country)
  }

  def getById(id: Long): Future[Option[UserAddress]] = db.run {
    userAddressTable.filter(_.id === id).result.headOption
  }

  def list(): Future[Seq[UserAddress]] = db.run {
    userAddressTable.result
  }

  def update(id: Long, new_userAddress: UserAddress): Future[Int] = db.run {
    val new_address: UserAddress = new_userAddress.copy(id)
    userAddressTable.filter(_.id === id).update(new_address)
  }

  def delete(id: Long): Future[Int] = db.run {
    userAddressTable.filter(_.id === id).delete
  }

}

