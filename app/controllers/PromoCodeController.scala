package controllers

import models.PromoCode
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.PromoCodeRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PromoCodeController @Inject()(promoCodeRepo: PromoCodeRepository, cc: MessagesControllerComponents)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def listPromoCodes: Action[AnyContent] = Action.async { implicit request =>
    promoCodeRepo.list().map(promoCodes => Ok(views.html.promoCodeList(promoCodes)))
  }

  def createPromoCode(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val promoCodes = promoCodeRepo.list()
    promoCodes.map(_ => Ok(views.html.promoCodeCreate(promoCodeForm)))
  }

  def createPromoCodeHandle(): Action[AnyContent] = Action.async { implicit request =>
    promoCodeForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.promoCodeCreate(errorForm))
        )
      },
      promoCode => {
        promoCodeRepo.create(promoCode.name, promoCode.amount).map { _ =>
          Redirect(routes.PromoCodeController.listPromoCodes)
        }
      }
    )
  }

  def getPromoCodeById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val promoCode = promoCodeRepo.getById(id)
    promoCode.map {
      case Some(p) => Ok(views.html.promoCode(p))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def updatePromoCode(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val promoCode = promoCodeRepo.getById(id)
    promoCode.map(code => {
      val prodForm = updatePromoCodeForm.fill(UpdatePromoCodeForm(code.get.id, code.get.name, code.get.amount))
      Ok(views.html.promoCodeUpdate(prodForm))
    })
  }

  def updatePromoCodeHandle(): Action[AnyContent] = Action.async { implicit request =>
    updatePromoCodeForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.promoCodeUpdate(errorForm))
        )
      },
      code => {
        promoCodeRepo.update(code.id, PromoCode(code.id, code.name, code.amount)).map { _ =>
          Redirect(routes.PromoCodeController.listPromoCodes)
        }
      }
    )
  }

  def deletePromoCode(id: Long): Action[AnyContent] = Action {
    promoCodeRepo.delete(id)
    Redirect(routes.PromoCodeController.listPromoCodes)
  }

  // utilities

  val promoCodeForm: Form[CreatePromoCodeForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "amount" -> number
    )(CreatePromoCodeForm.apply)(CreatePromoCodeForm.unapply)
  }

  val updatePromoCodeForm: Form[UpdatePromoCodeForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "amount" -> number,
    )(UpdatePromoCodeForm.apply)(UpdatePromoCodeForm.unapply)
  }
}

case class CreatePromoCodeForm(name: String, amount: Int)

case class UpdatePromoCodeForm(id: Long, name: String, amount: Int)