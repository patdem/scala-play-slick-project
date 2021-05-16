package controllers

import models.Category
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
import services.CategoryRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepository, cc: MessagesControllerComponents)
                                  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def listCategories: Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.list().map(categories => Ok(views.html.categoryList(categories)))
  }

  def createCategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()
    categories.map(_ => Ok(views.html.categoryCreate(categoryForm)))
  }

  def createCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.categoryCreate(errorForm))
        )
      },
      category => {
        categoryRepo.create(category.name).map { _ =>
          Redirect(routes.CategoryController.listCategories)
        }
      }
    )
  }

  def getCategoryById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val cat = categoryRepo.getById(id)
    cat.map {
      case Some(c) => Ok(views.html.category(c))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def updateCategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val category = categoryRepo.getById(id)
    category.map(category => {
      val catForm = updateCategoryForm.fill(UpdateCategoryForm(category.get.id, category.get.name))
      Ok(views.html.categoryUpdate(catForm))
    })
  }

  def updateCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.categoryUpdate(errorForm))
        )
      },
      category => {
        categoryRepo.update(category.id, Category(category.id, category.name)).map { _ =>
          Redirect(routes.CategoryController.listCategories)
        }
      }
    )
  }

  def deleteCategory(id: Long): Action[AnyContent] = Action {
    categoryRepo.delete(id)
    Redirect(routes.CategoryController.listCategories)
  }

  // utilities

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }
}

case class CreateCategoryForm(name: String)

case class UpdateCategoryForm(id: Long, name: String)
