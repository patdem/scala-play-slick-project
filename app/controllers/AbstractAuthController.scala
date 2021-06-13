package controllers

import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import models.UserAuth
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

abstract class AbstractAuthController(scc: DefaultSilhouetteControllerComponents)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  protected def authenticateUser(user: UserAuth)(implicit request: RequestHeader): Future[AuthenticatorResult] = {
    authenticatorService.create(user.loginInfo)
      .flatMap { authenticator =>
        authenticatorService.init(authenticator).flatMap { v =>
          authenticatorService.embed(v, Ok(user.email))
        }
      }
  }
}