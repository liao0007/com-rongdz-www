package controllers.wechat

import javax.inject.Inject

import _root_.services.SmsService
import auth._
import auth.filters.CookieSettings
import auth.providers.{WechatOAuth2Info, WechatProvider}
import auth.services.{JWTAuthService, UserLoginInfoService, WechatAuthService}
import com.mohiva.play.silhouette.api.Silhouette
import controllers.rest.Bad
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import views.html.helper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AccountController @Inject()(val messagesApi: MessagesApi,
                                  configuration: Configuration,
                                  silhouette: Silhouette[JWTEnv],
                                  smsService: SmsService,
                                  cookieSettings: CookieSettings,
                                  wechatProvider: WechatProvider,
                                  wechatAuthService: WechatAuthService,
                                  jWTAuthService: JWTAuthService,
                                  userLoginInfoService: UserLoginInfoService)
    extends Controller
    with I18nSupport {

  def signIn(returnUrl: String): Action[AnyContent] = Action.async { implicit request =>
    wechatProvider.withSettings { setting =>
      setting.copy(redirectURL = setting.redirectURL + "/" + helper.urlEncode(returnUrl))
    }.authenticate flatMap {
      case Left(result: Result)                      => Future.successful { result }
      case Right(wechatOAuth2Info: WechatOAuth2Info) =>
        //try to sign in
        wechatAuthService.signIn(wechatOAuth2Info.wxMpOAuth2AccessToken.getOpenId) flatMap checkUserState {
          // signed in
          case Right(SignedIn(_, token)) =>
            val response = Redirect(returnUrl)
            silhouette.env.authenticatorService.embed(token.token, response) map { authResult =>
              authResult.withCookies(cookieSettings.make(token.token))
            }

          //user not exists -> now sign up
          case Left(UserNotExists) =>
            Future.successful {
              Redirect(routes.AccountController.bind(wechatOAuth2Info.wxMpOAuth2AccessToken.getOpenId, returnUrl))
            }

            // user not activated -> reactivate
          case Left(UserNotActivated) =>
            Future.successful {
              Redirect(routes.AccountController.signUp(wechatOAuth2Info.wxMpOAuth2AccessToken.getOpenId, returnUrl))
            }
        }
    }
  }

  def signUp(openId: String, returnUrl: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    wechatProvider.withSettings { setting =>
      val protocol = if (request.secure) "https://" else "http://"
      setting.copy(redirectURL = protocol + request.host + request.uri, scope = Some("snsapi_userinfo"))
    }.authenticate flatMap {
      case Left(result: Result) => Future.successful { result }
      case Right(wechatOAuth2Info: WechatOAuth2Info) =>
        wechatProvider.retrieveProfile(wechatOAuth2Info) map { wechatProfile =>
          Ok(views.html.wechat.account.register(wechatProfile, openId, returnUrl))
        }
    }
  }

  def bind(openId: String, returnUrl: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Ok(views.html.wechat.account.bind(openId, returnUrl))
    }
  }

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

}
