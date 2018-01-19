package auth

import java.util.Locale

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import play.api.mvc.{Call, RequestHeader, Result}
import play.api.mvc.Results._

import scala.concurrent.Future

/**
  * Custom unsecured error handler.
  */
class CustomUnsecuredErrorHandler extends UnsecuredErrorHandler with CustomErrorHandler {

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = Future.successful {
    authenticatePf(request)(request.uri)
  }

}
