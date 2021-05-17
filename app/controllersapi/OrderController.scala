package controllersapi

import models.{Order, CreateOrder}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.OrderRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(val orderRepo: OrderRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[OrderController])

  def getOrderById(id: Long): Action[AnyContent] = Action.async {
    val order = orderRepo.getById(id)
    order.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("order not found")
    }
  }

  def listOrders(): Action[AnyContent] = Action.async {
    val categories = orderRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createOrder(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateOrder].map {
      order =>
        orderRepo.create(order.userId, order.paymentId, order.voucherId, order.promoCodeId).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateOrder(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Order].map {
      order =>
        orderRepo.update(order.id, order).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteOrder(id: Long): Action[AnyContent] = Action.async {
    orderRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

