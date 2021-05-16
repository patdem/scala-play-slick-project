package controllers

import models.{Basket, User, Product}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{BasketRepository, UserRepository, ProductRepository}

import javax.inject._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class BasketController @Inject()(basketRepo: BasketRepository, userRepo: UserRepository, productRepo: ProductRepository,
                                cc: MessagesControllerComponents)
                               (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()
  var productList: Seq[Product] = Seq[Product]()

  fetchData()

  def listBaskets: Action[AnyContent] = Action.async { implicit request =>
    basketRepo.list().map(baskets => Ok(views.html.basketList(baskets)))
  }

  def createBasket(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = Await.result(userRepo.list(), 1.second)
    val products = productRepo.list()

    products.map(product => Ok(views.html.basketCreate(basketForm, users, product)))
  }

  def createBasketHandle(): Action[AnyContent] = Action.async { implicit request =>
    basketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.basketCreate(errorForm, userList, productList))
        )
      },
      basket => {
        basketRepo.create(basket.userId, basket.productId).map { _ =>
          Redirect(routes.BasketController.listBaskets)
        }
      }
    )
  }

  def getBasketById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val basket = basketRepo.getById(id)
    basket.map {
      case Some(b) => Ok(views.html.basket(b))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def updateBasket(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val basket = basketRepo.getById(id)
    basket.map(basket => {
      val prodForm = updateBasketForm.fill(UpdateBasketForm(basket.get.id, basket.get.userId, basket.get.productId))
      Ok(views.html.basketUpdate(prodForm, userList, productList))
    })
  }

  def updateBasketHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateBasketForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.basketUpdate(errorForm, userList, productList))
        )
      },
      basket => {
        basketRepo.update(basket.id, Basket(basket.id, basket.userId, basket.productId)).map { _ =>
          Redirect(routes.BasketController.listBaskets)
        }
      }
    )
  }

  def deleteBasket(id: Long): Action[AnyContent] = Action {
    basketRepo.delete(id)
    Redirect(routes.BasketController.listBaskets)
  }

  // utilities

  val basketForm: Form[CreateBasketForm] = Form {
    mapping(
      "userId" -> longNumber,
      "productId" -> longNumber,
    )(CreateBasketForm.apply)(CreateBasketForm.unapply)
  }

  val updateBasketForm: Form[UpdateBasketForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "productId" -> longNumber,
    )(UpdateBasketForm.apply)(UpdateBasketForm.unapply)
  }

  def fetchData(): Unit = {

    userRepo.list().onComplete {
      case Success(users) => userList = users
      case Failure(e) => print("error while listing users", e)
    }

    productRepo.list().onComplete {
      case Success(product) => productList = product
      case Failure(e) => print("error while listing vouchers", e)
    }
  }
}

case class CreateBasketForm(userId: Long, productId: Long)

case class UpdateBasketForm(id: Long, userId: Long, productId: Long)