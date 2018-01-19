package auth

import play.api.mvc.Results.{Redirect, Status, Unauthorized}
import play.api.mvc.{Call, RequestHeader, Result}

import scala.concurrent.Future

/**
  * Created by liangliao on 9/1/17.
  */
trait CustomErrorHandler {
  protected def storeSignIn(f: RequestHeader => String)(implicit requestHeader: RequestHeader): Call = controllers.store.routes.AccountController.signIn(f(requestHeader))
  protected def wechatSignIn(f: RequestHeader => String)(implicit requestHeader: RequestHeader): Call = controllers.wechat.routes.AccountController.signIn(f(requestHeader))
  protected def isWechat(implicit requestHeader: RequestHeader) = requestHeader.headers.get("User-Agent").fold(false)(_.toLowerCase.contains("micromessenger"))

  protected def authenticatePf(requestHeader: RequestHeader): PartialFunction[String, Result] = {
    implicit val request = requestHeader

    {
      case uri if uri.startsWith("/store") && isWechat => Redirect(wechatSignIn(_.uri))
      case uri if uri.startsWith("/store") => Redirect(storeSignIn(_.uri))
      case uri if uri.startsWith("/admin") && isWechat => Redirect(wechatSignIn(_.uri))
      case uri if uri.startsWith("/admin") => Redirect(storeSignIn(_.uri))
      case uri if uri.startsWith("/rest") && isWechat => Unauthorized(wechatSignIn(_.headers.get("referer").getOrElse("/")).url)
      case uri if uri.startsWith("/rest") => Unauthorized(storeSignIn(_.headers.get("referer").getOrElse("/")).url)
      case _ => Unauthorized
    }
  }

}
