package controllersapi

import models.{CreditCard, CreateCreditCard}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.CreditCardRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreditCardController @Inject()(val creditCardRepo: CreditCardRepository, cc: ControllerComponents)
                                    (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[CreditCardController])

  def getCreditCardById(id: Long): Action[AnyContent] = Action.async {
    val creditCard = creditCardRepo.getById(id)
    creditCard.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("creditCard not found")
    }
  }

  def listCreditCards(): Action[AnyContent] = Action.async {
    val categories = creditCardRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createCreditCard(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateCreditCard].map {
      creditCard =>
        creditCardRepo.create(creditCard.userId, creditCard.cardName, creditCard.cardNumber,
          creditCard.expDate, creditCard.cvcCode).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateCreditCard(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreditCard].map {
      creditCard =>
        creditCardRepo.update(creditCard.id, creditCard).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteCreditCard(id: Long): Action[AnyContent] = Action.async {
    creditCardRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

