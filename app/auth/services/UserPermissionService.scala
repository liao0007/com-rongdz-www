package auth.services

import models.user._

import scala.concurrent.Future

class UserPermissionService {

  /**
    * Finds permission - user pair
    */
  def find(permission: Permission, userId: Long): Future[Option[Permission]] = Future.successful {
      Permission.findBy("userId" -> userId, "permission" -> permission.toString)
    }

  /**
    * Revokes permission from user
    */
  def revoke(permission: Permission, userId: Long): Future[Unit] =
    Future.successful {
      Permission.findAllBy("userId" -> userId, "permission" -> permission.toString) foreach (_.delete())
    }

  /**
    * Lists all permission that user has assigned
    */
  def allOfUser(userId: Long): Future[Seq[Permission]] = Future.successful {
    User.find(userId) map { user =>
      user.permissions.toSeq.flatMap(userPermission => UserPermissionPermission.fromString(userPermission.permission))
    } getOrElse Seq.empty
  }

  /**
    * Grants permission to user
    */
  def grant(permission: Permission, userId: Long): Future[Boolean] =
    Future.successful {
      (for {
        user <- User.find(userId)
      } yield {
        Permission.findByOrCreate(Permission(userId = userId, permission = permission.toString), "userId", "permission")
        true
      }) getOrElse false
    }

  /**
    * Lists all possible permissions
    */
  def allPossible(): Future[Seq[Permission]] = Future.successful {
    UserPermissionPermission.all
  }
}
