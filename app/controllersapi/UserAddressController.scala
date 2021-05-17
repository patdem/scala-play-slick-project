package controllersapi

import models.{UserAddress, CreateUserAddress}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.UserAddressRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressController @Inject()(val userAddressRepo: UserAddressRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[UserAddressController])

  def getUserAddressById(id: Long): Action[AnyContent] = Action.async {
    val userAddress = userAddressRepo.getById(id)
    userAddress.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("userAddress not found")
    }
  }

  def listUserAddresses(): Action[AnyContent] = Action.async {
    val categories = userAddressRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createUserAddress(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateUserAddress].map {
      userAddress =>
        userAddressRepo.create(userAddress.userId, userAddress.firstname, userAddress.lastname, userAddress.address,
          userAddress.zipcode, userAddress.city, userAddress.country).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateUserAddress(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UserAddress].map {
      userAddress =>
        userAddressRepo.update(userAddress.id, userAddress).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteUserAddress(id: Long): Action[AnyContent] = Action.async {
    userAddressRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

