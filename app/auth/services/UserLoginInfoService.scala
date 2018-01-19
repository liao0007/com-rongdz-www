package auth.services

import com.mohiva.play.silhouette.api.{LoginInfo => SilhouetteLoginInfo}
import daos.default.user.LoginInfo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserLoginInfoService {

  def retrieve(loginInfo: SilhouetteLoginInfo): Future[Option[LoginInfo]] = {
    Future.successful {
      LoginInfo.findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey)
    }
  }

  def save(silhouetteLoginInfo: SilhouetteLoginInfo, userId: Long): Future[LoginInfo] =
    Future.successful {
      LoginInfo.findBy("providerId" -> silhouetteLoginInfo.providerID, "providerKey" -> silhouetteLoginInfo.providerKey, "userId" -> userId) match {
        case Some(loginInfo) =>
          loginInfo.providerKey = silhouetteLoginInfo.providerKey
          loginInfo.update
        case None =>
          LoginInfo(userId, silhouetteLoginInfo.providerID, silhouetteLoginInfo.providerKey).create
      }
    }
}
