package utils

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.{UserAuth}

trait DefaultEnv extends Env {
  type I = UserAuth
  type A = CookieAuthenticator
}