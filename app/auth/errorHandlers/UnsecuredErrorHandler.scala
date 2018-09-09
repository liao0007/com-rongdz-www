package auth.errorHandlers

import com.google.inject.Inject
import controllers.Default
import play.api.mvc.Results.Unauthorized
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

/**
  * Custom unsecured error handler.
  */
class UnsecuredErrorHandler @Inject()(val defaultController: Default) extends com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler with ErrorHandler {

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = Future.successful {
    dispatchError(Unauthorized)(request)(request.uri)
  }

}
