package auth.services

import com.mohiva.play.silhouette.api.{LoginInfo => SilhouetteLoginInfo}

import scala.concurrent.Future

class UserLoginInfoService {

  def retrieve(loginInfo: SilhouetteLoginInfo): Future[Option[UserLoginInfo]] = {
    Future.successful {
      UserLoginInfo.findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey)
    }
  }

  def save(silhouetteLoginInfo: SilhouetteLoginInfo, userId: Long): Future[UserLoginInfo] =
    Future.successful {
      UserLoginInfo.findBy("providerId" -> silhouetteLoginInfo.providerID, "providerKey" -> silhouetteLoginInfo.providerKey, "userId" -> userId) match {
        case Some(loginInfo) =>
          loginInfo.providerKey = silhouetteLoginInfo.providerKey
          loginInfo.update
        case None =>
          UserLoginInfo(userId, silhouetteLoginInfo.providerID, silhouetteLoginInfo.providerKey).create
      }
    }
}
