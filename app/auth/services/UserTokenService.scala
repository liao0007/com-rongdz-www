package auth.services

import java.util.UUID

import com.google.inject.Inject
import com.github.aselab.activerecord.dsl._
import daos.default.user.Token
import daos.default.user.Token.Action
import daos.default.user.Token.TokenAction._
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserTokenService @Inject()(hashService: PasswordHashService, tokenService: TokenService) {

  /**
    * Issues a new token. New token is stored so it can later be claimed.
    */
  def issue(userId: Long, action: Action, forMinutes: Int): Future[Token] =
    Future.successful {

      val tokenString = action match {
        case ActivateAccount => tokenService.hash(UUID.randomUUID.toString, 4)
        case ResetPassword   => tokenService.hash(UUID.randomUUID.toString, 4)
      }

      Token(userId = userId, action = action.toString, token = tokenString, expiresOn = DateTime.now.plusMinutes(forMinutes)).create
    }

  /**
    * Consumes token if found. Onces token is consumed, it cannot be found again.
    */
  def consume(token: Token): Future[Boolean] = Future.successful {
    token.delete()
  }

  def consume(userId: Long, token: String): Future[Boolean] = Future.successful {
    Token.findAllBy("userId" -> userId, "token" -> token) foreach (_.delete())
    true
  }

  /**
    * Finds token if found and returns it.
    */
  def retrieve(userId: Long, token: String): Future[Option[Token]] =
    Future.successful {
      Token.findBy("token" -> token, "userId" -> userId)
    }
}
