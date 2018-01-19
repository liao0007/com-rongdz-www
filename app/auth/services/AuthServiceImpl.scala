package auth.services

import auth._
import auth.providers.WechatProvider
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{Silhouette, LoginInfo => SilhouetteLoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import daos.default.user.User.UserState
import play.api.libs.json._
import play.api.mvc.RequestHeader
import daos.default.user.{Token, User}
import daos.default.user.Token.TokenAction

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AuthService {
  def userIdentityService: UserIdentityService
  def userTokenService: UserTokenService
  def silhouette: Silhouette[JWTEnv]
  /**
    * Creates custom claims for particular user to be embedded in jwt token
    */
  protected def createCustomClaims(user: User): JsObject = Json.obj("user_id" -> user.id)

  protected def userStatusCheck[T](
      pf: PartialFunction[Option[User], Future[Either[Nothing, T]]]): PartialFunction[Option[User], Future[Either[UserStateError, T]]] = {
    val userStatePf: PartialFunction[Option[User], Future[Either[UserStateError, Nothing]]] = {
      case Some(user) if user.state == implicitly[String](UserState.Created)   => Future.successful { Left(UserNotActivated) }
      case Some(user) if user.state == implicitly[String](UserState.Activated) => Future.successful { Left(UserAlreadyExists) }
      case Some(user) if user.state == implicitly[String](UserState.Disabled)  => Future.successful { Left(UserDisabled) }
      case None                                                                => Future.successful { Left(UserNotExists) }
    }
    pf orElse userStatePf
  }

  protected def createJWTToken(loginInfo: SilhouetteLoginInfo, user: User)(
      implicit request: RequestHeader): Future[Right[Nothing, SignedIn]] =
    for {
      authenticator <- silhouette.env.authenticatorService.create(loginInfo).map(_.copy(customClaims = Some(createCustomClaims(user))))
      tokenValue    <- silhouette.env.authenticatorService.init(authenticator)
      expiration = authenticator.expirationDateTime
    } yield {
      Right(SignedIn(user.id, AuthToken(token = tokenValue, expiresOn = expiration)))
    }

  def requestSmsToken(silhouetteLoginInfo: SilhouetteLoginInfo, tokenAction: Token.Action, validForMinutes: Int)(
    implicit request: RequestHeader): Future[Either[UserStateError, SmsTokenGenerated]] = {
    userIdentityService.retrieve(silhouetteLoginInfo) flatMap userStatusCheck {
      case Some(user) =>
        userTokenService.issue(user.id, tokenAction, validForMinutes) map { token =>
          Right(SmsTokenGenerated(AuthToken(token = token.token, expiresOn = token.expiresOn)))
        }
    }
  }

}

class JWTAuthService @Inject()(
    val silhouette: Silhouette[JWTEnv],
    val userIdentityService: UserIdentityService,
    val userTokenService: UserTokenService,
    credentialsProvider: CredentialsProvider,
    passwordHasher: PasswordHasher,
    authInfoRepository: AuthInfoRepository,
    userLoginInfoService: UserLoginInfoService
) extends AuthService {

  def signUp(signUp: SignUp, validForMinutes: Int)(implicit request: RequestHeader): Future[Either[UserStateError, UserCreated]] = {
    val loginInfo = SilhouetteLoginInfo(CredentialsProvider.ID, signUp.identifier)
    userIdentityService.retrieve(loginInfo) flatMap userStatusCheck {
      case None =>
        User.transaction {
          val user = User(name = signUp.name, mobile = Some(signUp.identifier), state = UserState.Created, avatar = signUp.avatar).create
          userLoginInfoService.save(loginInfo, user.id)
          userTokenService.issue(user.id, TokenAction.ActivateAccount, validForMinutes) map { signUpToken =>
            Right(UserCreated(user.id, Some(signUpToken.token)))
          }
        }
    }
  }

  def signIn(signIn: SignIn)(implicit request: RequestHeader): Future[Either[UserStateError, SignedIn]] = {
    val SignIn(identifier, password, rememberMe) = signIn
    credentialsProvider.authenticate(Credentials(identifier, password)) flatMap { loginInfo =>
      userIdentityService.retrieve(loginInfo) flatMap userStatusCheck {
        case Some(user) if user.state == implicitly[String](UserState.Activated) => createJWTToken(loginInfo, user)
      }
    }
  }

  def updatePassword(passwordUpdateRequest: PasswordUpdateRequest)(
      implicit request: RequestHeader): Future[Either[UserStateError, PasswordUpdated]] = {
    val PasswordUpdateRequest(identifier, password) = passwordUpdateRequest
    val loginInfo                                   = SilhouetteLoginInfo(CredentialsProvider.ID, identifier)
    userIdentityService.retrieve(loginInfo) flatMap userStatusCheck {
      case Some(user) =>
        val authInfo = passwordHasher.hash(password)
        authInfoRepository.save(loginInfo, authInfo) map (passwordInfo => Right(PasswordUpdated(passwordInfo)))
    }
  }


}

class WechatAuthService @Inject()(
    val silhouette: Silhouette[JWTEnv],
    val userIdentityService: UserIdentityService,
    val userTokenService: UserTokenService,
    userLoginInfoService: UserLoginInfoService,
    jWTAuthService: JWTAuthService
) extends AuthService {

  def signUp(openId: String, signUp: SignUp, validForMinutes: Int)(implicit request: RequestHeader): Future[Either[UserStateError, UserCreated]] = {
    jWTAuthService.signUp(signUp, validForMinutes) map {
      case userCreated @ Right(UserCreated(userId, token)) =>
        val wechatLoginInfo = SilhouetteLoginInfo(WechatProvider.ID, openId)
        userLoginInfoService.save(wechatLoginInfo, userId)
        userCreated
      case cascade => cascade
    }
  }

  def bind(openId: String, signIn: SignIn)(implicit request: RequestHeader): Future[Either[UserStateError, SignedIn]] = {
    jWTAuthService.signIn(signIn) map {
      case signedIn @ Right(SignedIn(userId, token)) =>
        val loginInfo = SilhouetteLoginInfo(WechatProvider.ID, openId)
        userLoginInfoService.save(loginInfo, userId)
        signedIn
      case cascade => cascade
    }
  }

  def signIn(openId: String)(implicit request: RequestHeader): Future[Either[UserStateError, SignedIn]] = {
    val loginInfo = SilhouetteLoginInfo(WechatProvider.ID, openId)
    userIdentityService.retrieve(loginInfo) flatMap userStatusCheck {
      case Some(user) if user.state == implicitly[String](UserState.Activated) => createJWTToken(loginInfo, user)
    }
  }

}
