package auth.filters

import akka.stream.Materializer
import auth.environments.JWTEnv
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.UserAwareRequest
import javax.inject.Inject
import play.api.cache.AsyncCacheApi
import play.api.mvc._

import scala.concurrent.Future

class CacheFilter @Inject()(val silhouette: Silhouette[JWTEnv],
                            val cacheApi: AsyncCacheApi,
                            implicit val mat: Materializer) extends Filter {

  override def apply(f: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    val action = silhouette.UserAwareAction.async { request: UserAwareRequest[JWTEnv, _] =>
      request.identity match {
        case Some(_) => cacheApi.set("currentUser", cacheApi)
        case None => cacheApi.remove("currentUser")
      }
      f(request)
    }
    action(request).run
  }
}
