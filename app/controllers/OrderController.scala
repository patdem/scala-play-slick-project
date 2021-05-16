package controllers

import models.{Order, Payment, User, Voucher, PromoCode}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{OrderRepository, PaymentRepository, UserRepository, VoucherRepository, PromoCodeRepository}

import javax.inject._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class OrderController @Inject()(orderRepo: OrderRepository, userRepo: UserRepository, paymentRepo: PaymentRepository,
                                voucherRepo: VoucherRepository, promoCodeRepo: PromoCodeRepository,
                                cc: MessagesControllerComponents)
                               (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()
  var paymentList: Seq[Payment] = Seq[Payment]()
  var voucherList: Seq[Voucher] = Seq[Voucher]()
  var promoCodeList: Seq[PromoCode] = Seq[PromoCode]()

  fetchData()

  def listOrders: Action[AnyContent] = Action.async { implicit request =>
    orderRepo.list().map(orders => Ok(views.html.orderList(orders)))
  }

  def createOrder(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), 1.second)
    val payments = Await.result(paymentRepo.list(), 1.second)
    val vouchers = Await.result(voucherRepo.list(), 1.second)
    val promoCodes = promoCodeRepo.list()

    promoCodes.map(promoCode => Ok(views.html.orderCreate(orderForm, users, payments, vouchers, promoCode)))
  }

  def getOrderById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val order = orderRepo.getById(id)
    order.map {
      case Some(o) => Ok(views.html.order(o))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def createOrderHandle(): Action[AnyContent] = Action.async { implicit request =>
    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderCreate(errorForm, userList, paymentList, voucherList, promoCodeList))
        )
      },
      order => {
        orderRepo.create(order.userId, order.paymentId, order.voucherId, order.promoCodeId).map { _ =>
          Redirect(routes.OrderController.listOrders)
        }
      }
    )
  }

  def updateOrder(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val order = orderRepo.getById(id)
    order.map(order => {
      val prodForm = updateOrderForm.fill(UpdateOrderForm(order.get.id, order.get.userId, order.get.paymentId,
        order.get.voucherId, order.get.promoCodeId))
      Ok(views.html.orderUpdate(prodForm, userList, paymentList, voucherList, promoCodeList))
    })
  }

  def updateOrderHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderUpdate(errorForm, userList, paymentList, voucherList, promoCodeList))
        )
      },
      order => {
        orderRepo.update(order.id, Order(order.id, order.userId, order.paymentId,
          order.voucherId, order.promoCodeId)).map { _ =>
          Redirect(routes.OrderController.listOrders)
        }
      }
    )
  }

  def deleteOrder(id: Long): Action[AnyContent] = Action {
    orderRepo.delete(id)
    Redirect(routes.OrderController.listOrders)
  }

  // utilities

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "userId" -> longNumber,
      "paymentId" -> longNumber,
      "voucherId" -> longNumber,
      "promoCodeId" -> longNumber,
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "paymentId" -> longNumber,
      "voucherId" -> longNumber,
      "promoCodeId" -> longNumber,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def fetchData(): Unit = {

    userRepo.list().onComplete {
      case Success(users) => userList = users
      case Failure(e) => print("error while listing users", e)
    }

    paymentRepo.list().onComplete {
      case Success(payment) => paymentList = payment
      case Failure(e) => print("error while listing payments", e)
    }

    voucherRepo.list().onComplete {
      case Success(voucher) => voucherList = voucher
      case Failure(e) => print("error while listing vouchers", e)
    }

    promoCodeRepo.list().onComplete {
      case Success(promoCode) => promoCodeList = promoCode
      case Failure(e) => print("error while listing vouchers", e)
    }
  }
}

case class CreateOrderForm(userId: Long, paymentId: Long, voucherId: Long, promoCodeId: Long)

case class UpdateOrderForm(id: Long, userId: Long, paymentId: Long, voucherId: Long, promoCodeId: Long)
