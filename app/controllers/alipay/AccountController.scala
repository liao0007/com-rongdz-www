package controllers.alipay

import javax.inject.Inject

import _root_.services.SmsService
import auth._
import auth.filters.CookieSettings
import auth.providers.{WechatOAuth2Info, WechatProvider}
import auth.services.{JWTAuthService, UserLoginInfoService, WechatAuthService}
import com.mohiva.play.silhouette.api.{Silhouette, LoginInfo => SilhouetteLoginInfo}
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
    Future.successful(NotImplemented)

  }


}
