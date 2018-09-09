package auth.authorizations

import auth.services.UserPermissionService
import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import javax.inject.Inject
import models.user.{User, Permission => UserPermission}
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Authorizor that hits database (potentially cache) for each permission request
  */

trait Has {
  def permission(permissions: Seq[UserPermission]): Authorization[User, JWTAuthenticator]

  def permission(permission: UserPermission): Authorization[User, JWTAuthenticator] = this.permission(Seq(permission))
}

class HasImpl @Inject()(userPermissionService: UserPermissionService) extends Has {
  def permission(permissions: Seq[UserPermission]): Authorization[User, JWTAuthenticator] = {
    new Authorization[User, JWTAuthenticator] {
      override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = {
        checkPermission(permissions, identity)
      }
    }
  }

  private def checkPermission(permissions: Seq[UserPermission], identity: User): Future[Boolean] = permissions match {
    case head :: tail => userPermissionService.find(head, identity.id) flatMap {
      case Some(_) => Future.successful(true)
      case _ => this.checkPermission(tail, identity)
    }
    case _ => Future.successful(false)
  }

}