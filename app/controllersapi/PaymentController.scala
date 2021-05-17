package controllersapi

import models.{Payment, CreatePayment}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.PaymentRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentController @Inject()(val paymentRepo: PaymentRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[PaymentController])

  def getPaymentById(id: Long): Action[AnyContent] = Action.async {
    val payment = paymentRepo.getById(id)
    payment.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("payment not found")
    }
  }

  def listPayments(): Action[AnyContent] = Action.async {
    val categories = paymentRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createPayment(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreatePayment].map {
      payment =>
        paymentRepo.create(payment.userId, payment.creditCardId, payment.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updatePayment(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Payment].map {
      payment =>
        paymentRepo.update(payment.id, payment).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deletePayment(id: Long): Action[AnyContent] = Action.async {
    paymentRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

