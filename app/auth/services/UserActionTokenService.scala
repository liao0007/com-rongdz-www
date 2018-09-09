package auth.services

import java.util.UUID

import javax.inject.Inject
import models.user.{Token => UserToken}
import org.joda.time.DateTime

import scala.concurrent.Future

class UserActionTokenService @Inject()(hashService: PasswordHashService, tokenService: TokenService) {

  /**
    * Issues a new token. New token is stored so it can later be claimed.
    */
  def issue(userId: Long, action: UserToken.Action, forMinutes: Int = 30): Future[UserToken] = Future.successful {
    val tokenString = action match {
      case UserToken.Actions.ActivateAccount => tokenService.hash(UUID.randomUUID.toString, 4)
      case UserToken.Actions.ResetPassword => tokenService.hash(UUID.randomUUID.toString, 4)
      case _ => tokenService.hash(UUID.randomUUID.toString, 4)
    }

    UserToken(userId = userId, action = action.toString, token = tokenString, expiresOn = DateTime.now.plusMinutes(forMinutes)).create
  }

  /**
    * Consumes token if found. Onces token is consumed, it cannot be found again.
    */
  def consume(token: UserToken): Future[Boolean] = Future.successful {
    token.delete()
  }

  def consume(userId: Long, token: String): Future[Boolean] = Future.successful {
    UserToken.findAllBy("userId" -> userId, "token" -> token).deleteAll()
    true
  }

  /**
    * Finds token if found and returns it.
    */
  def retrieve(userId: Long, token: String): Future[Option[UserToken]] = Future.successful {
    UserToken.findBy("token" -> token, "userId" -> userId)
  }
}
