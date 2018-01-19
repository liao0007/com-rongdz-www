package auth.filters

import javax.inject.Inject

import akka.stream.Materializer
import auth.JWTEnv
import com.mohiva.play.silhouette.api.Silhouette
import play.api.cache.CacheApi
import play.api.mvc._

import scala.concurrent.Future

class CacheFilter @Inject()(val silhouette: Silhouette[JWTEnv],
                            val cacheApi: CacheApi,
                            implicit val mat: Materializer)
    extends Filter {

  override def apply(f: RequestHeader => Future[Result])(
      request: RequestHeader): Future[Result] = {

    val action = silhouette.UserAwareAction.async { r =>
      r.identity match {
        case Some(_) => cacheApi.set("currentUser", cacheApi)
        case None => cacheApi.remove("currentUser")
      }
      f(request)
    }

    action(request).run
  }
}
