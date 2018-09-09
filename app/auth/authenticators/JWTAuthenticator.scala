package auth.authenticators

import entities.{auth, _}
import auth.environments.JWTEnv
import auth.services.{UserActionTokenService, UserIdentityService, UserLoginInfoService}
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import entities.auth._
import entities.user.{PasswordUpdateRequest, PasswordUpdateResponse, RegistrationRequest, SmsTokenResponse}
import auth.entities
import javax.inject.Inject
import models.user.User.State
import models.user.{User, Token => UserToken}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.RequestHeader

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JWTAuthenticator @Inject()(
                                  silhouette: Silhouette[JWTEnv],
                                  userIdentityService: UserIdentityService,
                                  userActionTokenService: UserActionTokenService,
                                  credentialsProvider: CredentialsProvider,
                                  passwordHasher: PasswordHasher,
                                  authInfoRepository: AuthInfoRepository,
                                  userLoginInfoService: UserLoginInfoService
                                ) {

  /**
    * Creates custom claims for particular user to be embedded in jwt token
    */
  protected def createCustomClaims(user: User): JsObject = Json.obj("user_id" -> user.id)

  protected def userStatusCheck[T](
                                    pf: PartialFunction[Option[User], Future[Either[Nothing, T]]]): PartialFunction[Option[User], Future[Either[UserStateError, T]]] = {
    val userStatePf: PartialFunction[Option[User], Future[Either[UserStateError, Nothing]]] = {
      case Some(user) if user.state == implicitly[String](State.Created) => Future.successful(Left(UserNotActivated))
      case Some(user) if user.state == implicitly[String](State.Activated) => Future.successful(Left(UserAlreadyExists))
      case Some(user) if user.state == implicitly[String](State.Disabled) => Future.successful(Left(UserDisabled))
      case None => Future.successful(Left(UserNotExists))
    }
    pf orElse userStatePf
  }

  protected def createJWTToken(loginInfo: com.mohiva.play.silhouette.api.LoginInfo, user: User)(implicit request: RequestHeader): Future[Right[Nothing, Token]] = {
    for {
      authenticator <- silhouette.env.authenticatorService.create(loginInfo).map(_.copy(customClaims = Some(createCustomClaims(user))))
      tokenValue <- silhouette.env.authenticatorService.init(authenticator)
      expiration = authenticator.expirationDateTime
    } yield {
      Right(auth.Token(token = tokenValue, expiresOn = expiration))
    }
  }

  def createSmsToken(silhouetteLoginInfo: com.mohiva.play.silhouette.api.LoginInfo, tokenAction: UserToken.Action, validForMinutes: Int)(
    implicit request: RequestHeader): Future[Either[UserStateError, SmsTokenResponse]] = {
    userIdentityService.retrieve(silhouetteLoginInfo) flatMap userStatusCheck {
      case Some(user) =>
        userActionTokenService.issue(user.id, tokenAction, validForMinutes) map { token =>
          Right(SmsTokenResponse(auth.Token(token = token.token, expiresOn = token.expiresOn)))
        }
    }
  }

  def register(signUpRequest: RegistrationRequest, validForMinutes: Int = 30)(implicit request: RequestHeader): Future[Either[UserStateError, Token]] = {
    val loginInfo = com.mohiva.play.silhouette.api.LoginInfo(CredentialsProvider.ID, signUpRequest.identifier)
    userIdentityService.retrieve(loginInfo) flatMap userStatusCheck {
      case None =>
        User.transaction {
          val user = User(name = signUpRequest.name, mobile = Some(signUpRequest.identifier), state = State.Created, avatar = signUpRequest.avatar).create
          userLoginInfoService.save(loginInfo, user.id)
          userActionTokenService.issue(user.id, UserToken.Actions.ActivateAccount, validForMinutes) map { token =>
            Right(Token(token.token, token.expiresOn))
          }
        }
    }
  }

  def authenticate(authRequest: AuthRequest)(implicit request: RequestHeader): Future[Either[UserStateError, Token]] = {
    val AuthRequest(identifier, password) = authRequest

    credentialsProvider.authenticate(Credentials(identifier, password)) flatMap { loginInfo =>
      userIdentityService.retrieve(loginInfo) flatMap userStatusCheck {
        case Some(user) if user.state == implicitly[String](State.Activated) => createJWTToken(loginInfo, user)
      }
    } recover {
      case _: InvalidPasswordException => Left(UserNotExists)
      case _: IdentityNotFoundException => Left(UserNotExists)
    }
  }

  def updatePassword(passwordUpdateRequest: PasswordUpdateRequest)(
    implicit request: RequestHeader): Future[Either[UserStateError, PasswordUpdateResponse]] = {
    val PasswordUpdateRequest(identifier, password) = passwordUpdateRequest
    val loginInfo = com.mohiva.play.silhouette.api.LoginInfo(CredentialsProvider.ID, identifier)
    userIdentityService.retrieve(loginInfo) flatMap userStatusCheck {
      case Some(user) =>
        val authInfo = passwordHasher.hash(password)
        authInfoRepository.save(loginInfo, authInfo) map (passwordInfo => Right(PasswordUpdateResponse(passwordInfo = passwordInfo)))
    }
  }
}