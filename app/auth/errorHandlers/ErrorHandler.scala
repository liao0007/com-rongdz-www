package auth.errorHandlers

import controllers.Default
import play.api.mvc.Results._
import play.api.mvc.{Call, RequestHeader, Result}

/**
  * Created by liangliao on 9/1/17.
  */
trait ErrorHandler {
  val defaultController: Default

  protected def wwwSignIn(f: RequestHeader => String)(implicit requestHeader: RequestHeader): Call = controllers.www.routes.AccountController.signIn(f(requestHeader))

  //    protected def wechatSignIn(f: RequestHeader => String)(implicit requestHeader: RequestHeader): Call = controllers.wechat.routes.AccountController.signIn(f(requestHeader))
  //    protected def isWechat(implicit requestHeader: RequestHeader) = requestHeader.headers.get("User-Agent").fold(false)(_.toLowerCase.contains("micromessenger"))

  protected def dispatchError(status: Status)(implicit request: RequestHeader): PartialFunction[String, Result] = {
    case uri if uri.startsWith("/api") => status
    //      case uri if uri.startsWith("/store") => Redirect(storeSignIn(_.uri))
    //      case uri if uri.startsWith("/admin") && isWechat => Redirect(wechatSignIn(_.uri))
    case uri if uri.startsWith("/admin") => defaultController.Redirect(wwwSignIn(_.uri))
    //      case uri if uri.startsWith("/rest") && isWechat => Unauthorized(wechatSignIn(_.headers.get("referer").getOrElse("/")).url)
    //      case uri if uri.startsWith("/rest") => Unauthorized(storeSignIn(_.headers.get("referer").getOrElse("/")).url)
    case _ => status
  }
}
