package controllers

import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.UserRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserInfoController @Inject()(userRepo: UserRepository, cc: MessagesControllerComponents)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def listUsers: Action[AnyContent] = Action.async { implicit request =>
    userRepo.list().map(users => Ok(views.html.userList(users)))
  }

  def createUser(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = userRepo.list()
    users.map(_ => Ok(views.html.userCreate(userForm)))
  }

  def createUserHandle(): Action[AnyContent] = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.userCreate(errorForm))
        )
      },
      user => {
        userRepo.create(user.email, user.nickname, user.password).map { _ =>
          Redirect(routes.UserInfoController.listUsers)
        }
      }
    )
  }

  def getUserById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val user = userRepo.getById(id)
    user.map {
      case Some(u) => Ok(views.html.user(u))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def updateUser(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val user = userRepo.getById(id)
    user.map(user => {
      val userForm = updateUserForm.fill(UpdateUserForm(user.get.id, user.get.email, user.get.nickname, user.get.password))
      Ok(views.html.userUpdate(userForm))
    })
  }

  def updateUserHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.userUpdate(errorForm))
        )
      },
      user => {
        userRepo.update(user.id, User(user.id, user.email, user.email, user.password)).map { _ =>
          Redirect(routes.UserInfoController.listUsers)
        }
      }
    )
  }

  def deleteUser(id: Long): Action[AnyContent] = Action {
    userRepo.delete(id)
    Redirect(routes.UserInfoController.listUsers)
  }

  // utilities

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "email" -> nonEmptyText,
      "nickname" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "email" -> nonEmptyText,
      "nickname" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }
}

case class CreateUserForm(email: String, nickname: String, password: String)

case class UpdateUserForm(id: Long, email: String, nickname: String, password: String)

