package controllers

import models.Voucher
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.VoucherRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VoucherController @Inject()(voucherRepo: VoucherRepository, cc: MessagesControllerComponents)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def listVouchers: Action[AnyContent] = Action.async { implicit request =>
    voucherRepo.list().map(vouchers => Ok(views.html.voucherList(vouchers)))
  }

  def createVoucher(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val vouchers = voucherRepo.list()
    vouchers.map(_ => Ok(views.html.voucherCreate(voucherForm)))
  }

  def createVoucherHandle(): Action[AnyContent] = Action.async { implicit request =>
    voucherForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.voucherCreate(errorForm))
        )
      },
      voucher => {
        voucherRepo.create(voucher.amount).map { _ =>
          Redirect(routes.VoucherController.listVouchers)
        }
      }
    )
  }

  def getVoucherById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val voucher = voucherRepo.getById(id)
    voucher.map {
      case Some(v) => Ok(views.html.voucher(v))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def updateVoucher(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val voucher = voucherRepo.getById(id)
    voucher.map(voucher => {
      val prodForm = updateVoucherForm.fill(UpdateVoucherForm(voucher.get.id, voucher.get.amount))
      Ok(views.html.voucherUpdate(prodForm))
    })
  }

  def updateVoucherHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateVoucherForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.voucherUpdate(errorForm))
        )
      },
      voucher => {
        voucherRepo.update(voucher.id, Voucher(voucher.id, voucher.amount)).map { _ =>
          Redirect(routes.VoucherController.listVouchers)
        }
      }
    )
  }

  def deleteVoucher(id: Long): Action[AnyContent] = Action {
    voucherRepo.delete(id)
    Redirect(routes.VoucherController.listVouchers)
  }

  // utilities

  val voucherForm: Form[CreateVoucherForm] = Form {
    mapping(
      "amount" -> number,
    )(CreateVoucherForm.apply)(CreateVoucherForm.unapply)
  }

  val updateVoucherForm: Form[UpdateVoucherForm] = Form {
    mapping(
      "id" -> longNumber,
      "amount" -> number,
    )(UpdateVoucherForm.apply)(UpdateVoucherForm.unapply)
  }
}

case class CreateVoucherForm(amount: Int)

case class UpdateVoucherForm(id: Long, amount: Int)