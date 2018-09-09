package auth.environments

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.user.User

/**
  * Environment used by Silhouette.
  * Specified type of our User class and what Authenticator do we use.
  */
trait JWTEnv extends Env {
  type I = User
  type A = JWTAuthenticator
}
