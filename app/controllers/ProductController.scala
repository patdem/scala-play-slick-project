package controllers

import models.{Category, Product}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import services.{CategoryRepository, ProductRepository}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ProductController @Inject()(productRepo: ProductRepository, categoryRepo: CategoryRepository,
                                  cc: MessagesControllerComponents)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  var categoryList: Seq[Category] = Seq[Category]()

  fetchData()

  def listProducts: Action[AnyContent] = Action.async { implicit request =>
    productRepo.list().map(products => Ok(views.html.productList(products)))
  }

  def createProduct(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()
    categories.map(category => Ok(views.html.productCreate(productForm,category)))
  }

  def createProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.productCreate(errorForm, categoryList))
        )
      },
      product => {
        productRepo.create(product.name, product.description, product.category).map { _ =>
          Redirect(routes.ProductController.listProducts)
        }
      }
    )
  }

  def getProductById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val product = productRepo.getById(id)
    product.map {
      case Some(p) => Ok(views.html.product(p))
      case None => Redirect(routes.HomeController.index())
    }
  }

  def updateProduct(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val product = productRepo.getById(id)
    product.map(product => {
      val prodForm = updateProductForm.fill(UpdateProductForm(product.get.id, product.get.name,
                                                product.get.description, product.get.category))
      Ok(views.html.productUpdate(prodForm, categoryList))
    })
  }

  def updateProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.productUpdate(errorForm, categoryList))
        )
      },
      product => {
        productRepo.update(product.id, Product(product.id, product.name, product.description, product.category)).map { _ =>
          Redirect(routes.ProductController.listProducts)
        }
      }
    )
  }

  def deleteProduct(id: Long): Action[AnyContent] = Action {
    productRepo.delete(id)
    Redirect(routes.ProductController.listProducts)
  }

  // utilities

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def fetchData(): Unit = {
    categoryRepo.list().onComplete {
      case Success(category) => categoryList = category
      case Failure(e) => print("error while listing categories", e)
    }
  }
}

case class CreateProductForm(name: String, description: String, category: Long)

case class UpdateProductForm(id: Long, name: String, description: String, category: Long)
