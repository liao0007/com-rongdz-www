package auth.services

import com.github.aselab.activerecord.dsl._
import daos.default.user._
import daos.default.user.ToPermission.{Permission, UserToPermission}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserPermissionService {

  /**
    * Finds permission - user pair
    */
  def find(permission: Permission, userId: Long): Future[Option[ToPermission]] =
    Future.successful {
      ToPermission.findBy("userId" -> userId, "permission" -> permission.toString)
    }

  /**
    * Revokes permission from user
    */
  def revoke(permission: Permission, userId: Long): Future[Unit] =
    Future.successful {
      ToPermission.findAllBy("userId" -> userId, "permission" -> permission.toString) foreach (_.delete())
    }

  /**
    * Lists all permission that user has assigned
    */
  def allOfUser(userId: Long): Future[Seq[Permission]] = Future.successful {
    User.find(userId) map { user =>
      user.toPermissions.toSeq.flatMap(userPermission => UserToPermission.fromString(userPermission.permission))
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
        ToPermission.findByOrCreate(ToPermission(userId = userId, permission = permission.toString), "userId", "permission")
        true
      }) getOrElse false
    }

  /**
    * Lists all possible permissions
    */
  def allPossible(): Future[Seq[Permission]] = Future.successful {
    UserToPermission.all
  }
}
