package controllersapi

import models.{PromoCode, CreatePromoCode}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.PromoCodeRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PromoCodeController @Inject()(val promoCodeRepo: PromoCodeRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[PromoCodeController])

  def getPromoCodeById(id: Long): Action[AnyContent] = Action.async {
    val promoCode = promoCodeRepo.getById(id)
    promoCode.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("promoCode not found")
    }
  }

  def listPromoCodes(): Action[AnyContent] = Action.async {
    val categories = promoCodeRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createPromoCode(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreatePromoCode].map {
      promoCode =>
        promoCodeRepo.create(promoCode.name, promoCode.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updatePromoCode(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[PromoCode].map {
      promoCode =>
        promoCodeRepo.update(promoCode.id, promoCode).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deletePromoCode(id: Long): Action[AnyContent] = Action.async {
    promoCodeRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

