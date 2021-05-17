package controllersapi

import models.{Category, CreateCategory}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.CategoryRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(val categoryRepo: CategoryRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[CategoryController])

  def getCategoryById(id: Long): Action[AnyContent] = Action.async {
    val category = categoryRepo.getById(id)
    category.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("category not found")
    }
  }

  def listCategories(): Action[AnyContent] = Action.async {
    val categories = categoryRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createCategory(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateCategory].map {
      category =>
        categoryRepo.create(category.name).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateCategory(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Category].map {
      category =>
        categoryRepo.update(category.id, category).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteCategory(id: Long): Action[AnyContent] = Action.async {
    categoryRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

