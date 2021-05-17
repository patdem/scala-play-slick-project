package controllersapi

import models.{Product, CreateProduct}
import play.api.Logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.ProductRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductController @Inject()(val productRepo: ProductRepository, cc: ControllerComponents)
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) {

  val logger: Logger = Logger(classOf[ProductController])

  def getProductById(id: Long): Action[AnyContent] = Action.async {
    val product = productRepo.getById(id)
    product.map {
      case Some(res) => Ok(Json.toJson(res))
      case None => NotFound("product not found")
    }
  }

  def listProducts(): Action[AnyContent] = Action.async {
    val categories = productRepo.list()
    categories.map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def createProduct(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateProduct].map {
      product =>
        productRepo.create(product.name, product.description, product.category).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest(s"${request.body} error")))
  }

  def updateProduct(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product].map {
      product =>
        productRepo.update(product.id, product).map { res =>
          Ok(Json.toJson(res))
        }
    }.getOrElse(Future.successful(BadRequest("error")))
  }

  def deleteProduct(id: Long): Action[AnyContent] = Action.async {
    productRepo.delete(id).map { res =>
      Ok(Json.toJson(res))
    }
  }
}

