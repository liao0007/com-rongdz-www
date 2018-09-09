package auth.services

import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.user._

import scala.concurrent.Future

class UserIdentityService extends IdentityService[User] {

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    Future.successful {
      models.user.UserLoginInfo.findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey).flatMap(_.user.toOption)
    }

  /**
    * Retrieves the user
    *
    * @return Some of user if found, otherwise None
    */
  def retrieve(userId: Long): Future[Option[User]] = Future.successful {
    User.find(userId)
  }

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def create(user: User): Future[User] = Future.successful {
    user.create
  }

  def update(user: User): Future[User] = Future.successful {
    user.update
  }

  /**
    * Lists all users
    */
  def list(): Future[Seq[User]] = Future.successful {
    User.all.toSeq
  }

  /**
    * Sets new state to user with `id`
    *
    * @return true if new state was set successfuly, otherwise false
    */
  def setState(userId: Long, newState: User.State): Future[Option[Boolean]] =
    Future.successful {
      User.find(userId) map { user =>
        user.state = newState.toString
        user.save()
      }
    }

}
