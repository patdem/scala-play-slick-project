//package controllers
//
//import models.CategoryRepository
//import play.api.data.Form
//import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
//import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents, MessagesRequest}
//
//import javax.inject.Inject
//import scala.concurrent.{ExecutionContext, Future}
//
//@Singleton
//class CategoryController @Inject()(categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
//
//  def listCategories: Action[AnyContent] = Action.async { implicit request =>
//    categoryRepo.list().map(categories => Ok(views.html.category_list(categories)))
//  }
//
//  def createCategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
//    val categories = categoryRepo.list()
//    categories.map(_ => Ok(views.html.category_create(categoryForm)))
//  }
//
//  def createCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
//    categoryForm.bindFromRequest.fold(
//      errorForm => {
//        Future.successful(
//          BadRequest(views.html.category_create(errorForm))
//        )
//      },
//      category => {
//        categoryRepo.create(category.name).map { _ =>
//          Redirect("/form/category/list")
//        }
//      }
//    )
//  }
//
//  def updateCategory(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
//    val category = categoryRepo.getByIdOption(id)
//    category.map(category => {
//      val prodForm = updateCategoryForm.fill(UpdateCategoryForm(category.get.id, category.get.name))
//      Ok(views.html.category_update(prodForm))
//    })
//  }
//
//  def updateCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
//    updateCategoryForm.bindFromRequest.fold(
//      errorForm => {
//        Future.successful(
//          BadRequest(views.html.category_update(errorForm))
//        )
//      },
//      category => {
//        categoryRepo.update(category.id, Category(category.id, category.name)).map { _ =>
//          Redirect("/form/category/list")
//        }
//      }
//    )
//  }
//
//  def deleteCategory(id: Long): Action[AnyContent] = Action {
//    categoryRepo.delete(id)
//    Redirect("/form/category/list")
//  }
//
//  // utilities
//
//  val categoryForm: Form[CreateCategoryForm] = Form {
//    mapping(
//      "name" -> nonEmptyText,
//    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
//  }
//
//  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
//    mapping(
//      "id" -> longNumber,
//      "name" -> nonEmptyText,
//    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
//  }
//}
//
//case class CreateCategoryForm(name: String)
//
//case class UpdateCategoryForm(id: Long, name: String)
