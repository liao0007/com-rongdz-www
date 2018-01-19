/**
  * Created by liangliao on 6/2/16.
  */
import javax.inject._

import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent._

class ErrorHandler @Inject()(
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router],
    val messagesApi: MessagesApi
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
    with I18nSupport {

  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    errorHandlePf(BadRequest, request)(request.uri)
  }

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    errorHandlePf(NotFound, request)(request.uri)
  }

  override def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    errorHandlePf(InternalServerError, request)(request.uri)
  }

  override def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    errorHandlePf(InternalServerError, request)(request.uri)
  }

  override def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    errorHandlePf(Forbidden, request)(request.uri)
  }

  private def errorHandlePf(status: Status, requestHeader: RequestHeader): PartialFunction[String, Future[Result]] = {
    implicit val request: RequestHeader = requestHeader

    {
      case uri: String if uri.startsWith("/store")  => Future.successful(status(views.html.store.error()))
      case uri: String if uri.startsWith("/admin")  => Future.successful(status(views.html.store.error()))
      case uri: String if uri.startsWith("/wechat") => Future.successful(status(views.html.wechat.error()))
      case _                                        => Future.successful(status)
    }
  }
}
