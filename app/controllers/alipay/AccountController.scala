package controllers.alipay

import _root_.services.SmsService
import auth.filters.CookieSettings
import auth.services.UserLoginInfoService
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

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
