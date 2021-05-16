package controllers

import models.{CreditCard, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{CreditCardRepository, UserRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CreditCardController @Inject()(creditCardRepo: CreditCardRepository, userRepo: UserRepository,
                                     cc: MessagesControllerComponents)
                                    (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var userList: Seq[User] = Seq[User]()

  fetchData()

  def listCreditCards: Action[AnyContent] = Action.async { implicit request =>
    creditCardRepo.list().map(creditCards => Ok(views.html.creditCardList(creditCards)))
  }

  def createCreditCard(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = userRepo.list()

    users.map(users => Ok(views.html.creditCardCreate(creditCardForm, users)))
  }

  def createCreditCardHandle(): Action[AnyContent] = Action.async { implicit request =>
    creditCardForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.creditCardCreate(errorForm, userList))
        )
      },
      creditCard => {
        creditCardRepo.create(creditCard.userId, creditCard.cardName, creditCard.cardNumber,
          creditCard.expDate, creditCard.cvcCode).map { _ =>
          Redirect(routes.CreditCardController.listCreditCards)
        }
      }
    )
  }

  def getCreditCardById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val card = creditCardRepo.getById(id)
    card.map {
      case Some(c) => Ok(views.html.creditCard(c))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def updateCreditCard(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val creditCard = creditCardRepo.getById(id)
    creditCard.map(creditCard => {
      val prodForm = updateCreditCardForm.fill(UpdateCreditCardForm(creditCard.get.id, creditCard.get.userId,
        creditCard.get.cardName, creditCard.get.cardNumber, creditCard.get.expDate, creditCard.get.cvcCode))
      Ok(views.html.creditCardUpdate(prodForm, userList))
    })
  }

  def updateCreditCardHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCreditCardForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.creditCardUpdate(errorForm, userList))
        )
      },
      creditCard => {
        creditCardRepo.update(creditCard.id, CreditCard(creditCard.id, creditCard.userId, creditCard.cardName,
          creditCard.cardNumber, creditCard.expDate, creditCard.cvcCode)).map { _ =>
          Redirect(routes.CreditCardController.listCreditCards)
        }
      }
    )
  }

  def deleteCreditCard(id: Long): Action[AnyContent] = Action {
    creditCardRepo.delete(id)
    Redirect(routes.CreditCardController.listCreditCards)
  }

  // utilities

  val creditCardForm: Form[CreateCreditCardForm] = Form {
    mapping(
      "userId" -> longNumber,
      "cardholderName" -> nonEmptyText,
      "number" -> nonEmptyText,
      "expDate" -> nonEmptyText,
      "cvcCode" -> nonEmptyText,
    )(CreateCreditCardForm.apply)(CreateCreditCardForm.unapply)
  }

  val updateCreditCardForm: Form[UpdateCreditCardForm] = Form {
    mapping(
      "id" -> longNumber,
      "userId" -> longNumber,
      "cardholderName" -> nonEmptyText,
      "number" -> nonEmptyText,
      "expDate" -> nonEmptyText,
      "cvcCode" -> nonEmptyText,
    )(UpdateCreditCardForm.apply)(UpdateCreditCardForm.unapply)
  }

  def fetchData(): Unit = {
    userRepo.list().onComplete {
      case Success(user) => userList = user
      case Failure(e) => print("error while listing users", e)
    }
  }
}

case class CreateCreditCardForm(userId: Long, cardName: String, cardNumber: String, expDate: String, cvcCode: String)

case class UpdateCreditCardForm(id: Long, userId: Long, cardName: String, cardNumber: String,
                                expDate: String, cvcCode: String)