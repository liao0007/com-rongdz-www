package auth.services.authorizations

import javax.inject.Inject

import auth.services.UserPermissionService
import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import daos.default.user.User
import daos.default.user.ToPermission.Permission
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Authorizer that hits database (potentially cache) for each permission request
  */

trait Has {
  def permission(permission: Permission): Authorization[User, JWTAuthenticator]
}

class HasImpl @Inject()(userPermissionService: UserPermissionService) extends Has {
  def permission(permission: Permission): Authorization[User, JWTAuthenticator] = {
    new Authorization[User, JWTAuthenticator] {
      override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = {
        userPermissionService.find(permission, identity.id) map (_.isDefined)
      }
    }
  }
}