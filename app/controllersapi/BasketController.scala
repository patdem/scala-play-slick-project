package controllersapi

import models.{Basket, CreateBasket}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.BasketRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BasketController @Inject()(val basketRepo: BasketRepository, cc: ControllerComponents)
                                (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[BasketController])

  def getBasketById(id: Long): Action[AnyContent] = Action.async {
    val basket = basketRepo.getById(id)
    basket.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("basket not found")
    }
  }

  def listBaskets(): Action[AnyContent] = Action.async {
    val categories = basketRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createBasket(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateBasket].map {
      basket =>
        basketRepo.create(basket.userId, basket.productId).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateBasket(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Basket].map {
      basket =>
        basketRepo.update(basket.id, basket).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteBasket(id: Long): Action[AnyContent] = Action.async {
    basketRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

