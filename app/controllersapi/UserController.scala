package controllersapi

import models.{User, CreateUser}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.UserRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(val userRepo: UserRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[UserController])

  def getUserById(id: Long): Action[AnyContent] = Action.async {
    val user = userRepo.getById(id)
    user.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("user not found")
    }
  }

  def listUsers(): Action[AnyContent] = Action.async {
    val categories = userRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateUser].map {
      user =>
        userRepo.create(user.email, user.nickname, user.password).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateUser(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User].map {
      user =>
        userRepo.update(user.id, user).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteUser(id: Long): Action[AnyContent] = Action.async {
    userRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

