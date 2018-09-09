package auth.errorHandlers

import controllers.Default
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

/**
  * Custom secured error handler.
  *
  * @param messagesApi The Play messages API.
  */
class SecuredErrorHandler @Inject()(val messagesApi: MessagesApi,
                                    val defaultController: Default,
                                    wwwErrorTemplate: views.html.www.Error,
                                   ) extends com.mohiva.play.silhouette.api.actions.SecuredErrorHandler with I18nSupport with ErrorHandler {

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = Future.successful {
    dispatchError(Unauthorized)(request)(request.uri)
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
      case uri: String if uri.startsWith("/api") => Unauthorized
      //      case uri: String if uri.startsWith("/admin")  => Unauthorized(views.html.store.error(Some("Unauthorized")))
      //      case uri: String if uri.startsWith("/wechat") => Unauthorized(views.html.wechat.error(Some("Unauthorized")))
      case _ => Unauthorized(wwwErrorTemplate(Some("Unauthorized")))
    }
  }
}
