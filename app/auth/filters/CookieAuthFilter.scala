package auth.filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.http.Status
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Remark:
  * Only class why we need to depend to play's filters. But that's fine, although we could do it more lightweight
  * if we require.
  */
/**
  * Filter which enables functionality like REMEMBER ME during login, or
  * single login/logout across multiple frontend projects
  *
  * If authentication token is present with request, it sets the cookie to remember it.
  * If it's not present, it tries to supply it using that cookie
  */
class CookieAuthFilter @Inject()(
                                  config: Configuration,
                                  implicit val mat: Materializer,
                                  implicit val ec: ExecutionContext,
                                ) extends Filter {

  private[this] val cookie = new CookieSettings(config)

  if (!cookie.secure || !cookie.httpOnly) {
    Logger(this.getClass).warn("check your cookie settings (filters.cookieauth.cookie), as they are not secure!")
  }

  override def apply(f: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    requestHeader.headers.get(cookie.tokenHeader) match {
      // Token was sent - discard current token cookie, and set it to new value
      case Some(token) =>
        f(requestHeader).map { result =>
          val withoutCookie = result.discardingCookies(DiscardingCookie(cookie.name))

          // Have we been denied entry? If yes, discard our cookie
          if (isDeniedEntry(result)) withoutCookie else withoutCookie.withCookies(cookie.make(token))
        }

      // Token was not sent - look into cookies and set to header
      case None =>
        requestHeader.cookies.get(cookie.name) match {
          // Cookie was found - transform it into X-Auth-Token
          case Some(found) =>
            val newRh = requestHeader.withHeaders(newHeaders = requestHeader.headers.add(cookie.tokenHeader -> found.value))
            val result = f(newRh)
            // Have we been denied entry in our result? If yes, discard cookie we sent (as it is invalid)
            result.map { withCookie =>
              if (isDeniedEntry(withCookie)) withCookie.discardingCookies(DiscardingCookie(cookie.name)) else withCookie
            }

          // Cookie was not found - process as you would
          case None =>
            f(requestHeader)
        }
    }
  }

  /**
    * Was request forbidden?
    */
  private def isDeniedEntry(result: Result): Boolean =
    result.header.status == Status.FORBIDDEN || result.header.status == Status.UNAUTHORIZED
}

class CookieSettings @Inject()(configuration: Configuration) {
  val config: Configuration = configuration.get[Configuration]("filters.cookieauth.cookie")
  val name: String = config.get[String]("name")
  val tokenHeader: String = config.get[String]("token.header")
  val maxAge: Option[Int] = {
    val age = config.get[Int]("maxage")
    if (age >= 0) Some(age) else None
  }
  val path: String = config.get[String]("path")
  val domain: Option[String] = {
    val domain = config.get[String]("domain")
    if (domain.length >= 0) Some(domain) else None
  }
  val secure: Boolean = config.get[Boolean]("secure")
  val httpOnly: Boolean = config.get[Boolean]("httpOnly")

  def make(value: String): Cookie = Cookie(name, value, maxAge, path, domain, secure, httpOnly)
}
