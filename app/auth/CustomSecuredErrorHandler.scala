package auth

import java.util.Locale
import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._

import scala.concurrent.Future

/**
  * Custom secured error handler.
  *
  * @param messagesApi The Play messages API.
  */
class CustomSecuredErrorHandler @Inject()(val messagesApi: MessagesApi) extends SecuredErrorHandler with I18nSupport with CustomErrorHandler {

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = Future.successful {
    authenticatePf(request)(request.uri)
  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = Future.successful {
    request.uri match {
      case uri: String if uri.startsWith("/store")  => Unauthorized(views.html.store.error(Some("无权限")))
      case uri: String if uri.startsWith("/admin")  => Unauthorized(views.html.store.error(Some("无权限")))
      case uri: String if uri.startsWith("/wechat") => Unauthorized(views.html.wechat.error(Some("无权限")))
      case _                                        => Unauthorized(views.html.store.error(Some("无权限")))
    }
  }
}
