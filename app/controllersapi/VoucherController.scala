package controllersapi

import models.{Voucher, CreateVoucher}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.VoucherRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VoucherController @Inject()(val voucherRepo: VoucherRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[VoucherController])

  def getVoucherById(id: Long): Action[AnyContent] = Action.async {
    val voucher = voucherRepo.getById(id)
    voucher.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("voucher not found")
    }
  }

  def listVouchers(): Action[AnyContent] = Action.async {
    val categories = voucherRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createVoucher(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateVoucher].map {
      voucher =>
        voucherRepo.create(voucher.amount).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateVoucher(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Voucher].map {
      voucher =>
        voucherRepo.update(voucher.id, voucher).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteVoucher(id: Long): Action[AnyContent] = Action.async {
    voucherRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

