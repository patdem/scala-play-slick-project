package controllers
import play.api.Logging
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Cookie, Request}
import play.filters.csrf.CSRF.Token
import play.filters.csrf.{CSRF, CSRFAddToken}

import scala.concurrent.{ExecutionContext, Future}

class SocialAuthController @Inject()(scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)
                                    (implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  def authenticate(provider: String): Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => {
            Future.successful(result)
          }
          case Right(authInfo) => {
            for {
              profile <- p.retrieveProfile(authInfo)
              _ <- userRepository.create(profile.loginInfo.providerID, profile.loginInfo.providerKey, profile.email.getOrElse("UNKNOWN"))
              _ <- authInfoRepository.save(profile.loginInfo, authInfo)
              authenticator <- authenticatorService.create(profile.loginInfo)
              value <- authenticatorService.init(authenticator)
              result <- authenticatorService.embed(value, Redirect(scala.util.Properties.envOrElse("FRONTEND_URL", "http://localhost:3000")))
            } yield {
              val Token(name, value) = CSRF.getToken.get
              logger.error(profile.toString)
              result.withCookies(Cookie(name, value, httpOnly = false), Cookie("email", profile.email.get, httpOnly = false))
            }
          }
        }
      case _ => {
        Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
      }
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Forbidden("Forbidden")
    }
  })
}