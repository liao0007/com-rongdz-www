package controllers.rest

import java.time.Duration

import auth.{PasswordUpdateRequest, UserAlreadyExists, UserNotActivated, _}
import auth.filters.CookieSettings
import services._
import _root_.services.SmsService
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette, LoginInfo => SilhouetteLoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import daos.default.user.Token.TokenAction
import daos.default.user.Token.TokenAction.{ActivateAccount, ResetPassword}
import daos.default.user.User.UserState
import daos.default.user._
import models.user.{Token, User}
import org.joda.time.DateTime
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc.Results.EmptyContent
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Left

/**
  * Sign in using login/password credentials (no 3d party social login).
  */
class AuthController @Inject()(
    silhouette: Silhouette[JWTEnv],
    cookieSettings: CookieSettings,
    jWTAuthService: JWTAuthService,
    wechatAuthService: WechatAuthService,
    configuration: Configuration,
    userPermissionService: UserPermissionService,
    userTokenService: UserTokenService,
    userIdentityService: UserIdentityService,
    smsService: SmsService
) extends Controller
    with ResponseSupport {

  //sign up
  val newAccountTokenValidFor: Duration = configuration.underlying.getDuration("tokens.sms-activateAccount.validfor")
  def signUp: Action[JsValue] = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    request.body
      .validate[SignUp]
      .map { signUp =>
        jWTAuthService.signUp(signUp, newAccountTokenValidFor.toMinutes.toInt) flatMap checkUserState {
          case Right(UserCreated(_, Some(token))) =>
            smsService.send(signUp.identifier, "您正在注册, 验证码:" + token.toUpperCase() + ", " + newAccountTokenValidFor.toMinutes + "分钟内有效") map { _ =>
              Created(Json.toJson(Good.empty))
            }

          case Left(UserNotActivated) =>
            jWTAuthService.requestSmsToken(SilhouetteLoginInfo(CredentialsProvider.ID, signUp.identifier),
                                           ActivateAccount,
                                           newAccountTokenValidFor.toMinutes.toInt) flatMap checkUserState {
              case Right(SmsTokenGenerated(authToken)) =>
                smsService.send(signUp.identifier,
                                "您正在注册, 验证码:" + authToken.token.toUpperCase() + ", " + newAccountTokenValidFor.toMinutes + "分钟内有效") map { _ =>
                  Created(Json.toJson(Good.empty))
                }
            }
        }
      }
      .recoverTotal(badRequestWithMessage)
  }

  //sign in
  def signIn: Action[JsValue] = silhouette.UnsecuredAction.async(parse.json) { implicit request =>
    request.body
      .validate[SignIn]
      .map { signIn =>
        internalSignIn(signIn)
      }
      .recoverTotal(badRequestWithMessage)
  }

  def changePassword: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { implicit request =>
    request.body
      .validate[PasswordUpdateRequest]
      .map { updatePasswordRequest =>
        jWTAuthService.updatePassword(updatePasswordRequest) flatMap checkUserState {
          case Right(PasswordUpdated(passwordInfo)) =>
            Future.successful { Created(Json.toJson(Good.empty)) }
        }
      }
      .recoverTotal(badRequestWithMessage)
  }

  //sign out
  def signOut: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { implicit request =>
    Future.successful {
      Ok(EmptyContent()).discardingCookies(DiscardingCookie(cookieSettings.name, cookieSettings.path, cookieSettings.domain, cookieSettings.secure))
    }
  }

  //bind wechat
  def socialBind(openId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[SignIn]
      .map { signIn =>
        wechatAuthService.bind(openId, signIn) flatMap checkUserState {
          case Right(SignedIn(userId, token)) =>
            val response = Ok(Json.toJson(token))
            silhouette.env.authenticatorService.embed(token.token, response) map { authResult =>
              if (signIn.rememberMe)
                authResult.withCookies(cookieSettings.make(token.token))
              else authResult
            }
        }
      }
      .recoverTotal(badRequestWithMessage)
  }

  //sign up wechat
  def socialSignUp(openId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[SignUp]
      .map { signUp =>
        wechatAuthService.signUp(openId, signUp, newAccountTokenValidFor.toMinutes.toInt) flatMap checkUserState {
          case Right(UserCreated(_, Some(token))) =>
            smsService.send(signUp.identifier, "您正在注册, 验证码:" + token.toUpperCase() + ", " + newAccountTokenValidFor.toMinutes + "分钟内有效") map { _ =>
              Created(Json.toJson(Good.empty))
            }

          case Left(UserNotActivated) =>
            wechatAuthService.requestSmsToken(SilhouetteLoginInfo(CredentialsProvider.ID, signUp.identifier),
                                              ActivateAccount,
                                              newAccountTokenValidFor.toMinutes.toInt) flatMap checkUserState {
              case Right(SmsTokenGenerated(authToken)) =>
                smsService.send(signUp.identifier,
                                "您正在注册, 验证码:" + authToken.token.toUpperCase() + ", " + newAccountTokenValidFor.toMinutes + "分钟内有效") map { _ =>
                  Created(Json.toJson(Good.empty))
                }
            }
        }
      }
      .recoverTotal(badRequestWithMessage)
  }

  //token
  /**
    * Fetches the token and executes it (consumes it). It is run only when it's valid (not expired.
    * @note Does not consume tokens by default, only on successful request!
    */
  val smsResetPasswordTokenValidFor: Duration = configuration.underlying.getDuration("tokens.sms-resetPassword.validfor")
  def requestSmsResetPasswordToken: Action[JsValue] = silhouette.UserAwareAction.async(parse.json) { implicit request =>
    request.body
      .validate[SmsTokenRequest]
      .map { smsTokenRequest =>
        jWTAuthService.requestSmsToken(SilhouetteLoginInfo(CredentialsProvider.ID, smsTokenRequest.identifier),
                                       ResetPassword,
                                       smsResetPasswordTokenValidFor.toMinutes.toInt) flatMap checkUserState {
          case Right(SmsTokenGenerated(token)) =>
            smsService.send(smsTokenRequest.identifier,
                            "您正在重置密码, 验证码:" + token.token.toUpperCase() + ", " + smsResetPasswordTokenValidFor.toMinutes + "分钟内有效") map { _ =>
              Created(Json.toJson(Good.empty))
            }
        }
      }
      .recoverTotal(badRequestWithMessage)
  }

  def execute(providerId: String, providerKey: String, tokenString: String): Action[AnyContent] = Action.async { implicit request =>
    userIdentityService.retrieve(com.mohiva.play.silhouette.api.LoginInfo(providerId, providerKey)) flatMap {
      case Some(user) =>
        userTokenService.retrieve(user.id, tokenString.toLowerCase()) flatMap {

          case Some(token) if !token.expiresOn.isBefore(DateTime.now) =>
            executeImpl(user, providerId, providerKey, token)

          case Some(token) =>
            // expired token
            userTokenService.consume(user.id, token.token) map { _ =>
              Conflict(Json.toJson(Bad("token.expired")))
            }

          case _ => Future.successful(NotFound(Json.toJson(Bad("invalid.token"))))
        }
      case _ => Future.successful { NotFound(Json.toJson(Bad("invalid.user"))) }
    }
  }

  /**
    * Do not forget to consume tokens upon successful request
    */
  private def executeImpl(user: User, providerId: String, providerKey: String, userToken: Token)(
      implicit request: Request[AnyContent]): Future[Result] =
    userToken match {
      //activate
      case Token(_, _, expiresOn, userTokenAction) if userTokenAction == implicitly[String](TokenAction.ActivateAccount) =>
        request.body.asJson match {
          case Some(json) =>
            json
              .validate[PasswordUpdateRequest]
              .map { passwordUpdateRequest =>
                jWTAuthService.updatePassword(passwordUpdateRequest) flatMap checkUserState {
                  case Right(passwordUpdated: PasswordUpdated) =>
                    for {
                      _ <- userIdentityService.setState(user.id, UserState.Activated)
                      _ <- userTokenService.consume(userToken)
                      signIn = SignIn(identifier = passwordUpdateRequest.identifier, password = passwordUpdateRequest.password)
                      signInResult <- internalSignIn(signIn)
                    } yield signInResult
                }
              }
              .recoverTotal(badRequestWithMessage)

          case None =>
            Future.successful(BadRequest(Json.toJson(Bad.invalidJson)))
        }

      //reset password
      case Token(_, _, _, userTokenAction) if userTokenAction == implicitly[String](TokenAction.ResetPassword) =>
        request.body.asJson match {
          case Some(json) =>
            json
              .validate[PasswordUpdateRequest]
              .map { passwordUpdateRequest =>
                jWTAuthService.updatePassword(passwordUpdateRequest) flatMap checkUserState {
                  case Right(passwordChanged: PasswordUpdated) =>
                    for {
                      _ <- userTokenService.consume(userToken)
                    } yield Ok
                }
              }
              .recoverTotal(badRequestWithMessage)

          case None =>
            Future.successful(BadRequest(Json.toJson(Bad.invalidJson)))
        }

      case _ => Future.successful(BadRequest(Json.toJson(Bad("unknown.token"))))
    }

  //permission
  def verify(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful(HandlerResult(Ok("All's good"), request.identity.mobile)) map {
      case HandlerResult(r, Some(data)) => Ok(data)
      case HandlerResult(r, None)       => Unauthorized
    }
  }

  def verifyRoles: Action[AnyContent] = ???

  private def checkUserState[T](pf: PartialFunction[Either[UserStateError, T], Future[Result]]) = {
    val userStatePf: PartialFunction[Either[UserStateError, T], Future[Result]] = {
      case Left(UserNotExists)      => Future.successful(Conflict(Json.toJson(Bad("state.not.exists"))))
      case Left(UserAlreadyExists)  => Future.successful(Conflict(Json.toJson(Bad("state.user.exists"))))
      case Left(UserNotActivated)   => Future.successful(Conflict(Json.toJson(Bad("state.not.activated"))))
      case Left(UserDisabled)       => Future.successful(Unauthorized(Json.toJson(Bad("state.disabled"))))
      case Left(LoginInfoMissing)   => Future.successful(Unauthorized(Json.toJson(Bad("info.missing"))))
      case Left(InvalidCredentials) => Future.successful(Unauthorized(Json.toJson(Bad("invalid.credentials"))))
      case Left(ProviderError(e))   => Future.successful(Unauthorized(Json.toJson(Bad("provider.error" + e.getLocalizedMessage))))
    }
    pf orElse userStatePf
  }

  private def internalSignIn(signIn: SignIn)(implicit request: RequestHeader) = {
    jWTAuthService.signIn(signIn) flatMap checkUserState {
      case Right(SignedIn(userId, token)) =>
        val response = Ok(Json.toJson(token))
        silhouette.env.authenticatorService.embed(token.token, response) map { authResult =>
          if (signIn.rememberMe)
            authResult.withCookies(cookieSettings.make(token.token))
          else authResult
        }
    }
  }

}
