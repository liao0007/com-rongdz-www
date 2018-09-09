package services.mall

import java.lang.Long

import com.google.inject.Inject
import models.mall.{CartItem, Sale}
import models.user.User

import scala.concurrent.Future

/**
  *  Created by liangliao on 7/28/16.
  */
class CartService @Inject()(val sedisPool: Pool) {

  val cartSedisKeyPrefix = "cart:"

  def update(userId: Long, saleNumber: String, quantity: Int): Future[Int] =
    Future.successful {
      sedisPool.withJedisClient { client =>
        client.hset(cartSedisKeyPrefix + userId, saleNumber, quantity.toString)
        client.hgetAll(cartSedisKeyPrefix + userId).asScala.toSeq.map(_._2.toInt).sum
      }
    }

  def list(userId: Long): Future[Seq[CartItem]] = Future.successful {
    sedisPool.withJedisClient(client => client.hgetAll(cartSedisKeyPrefix + userId).asScala.toSeq) flatMap {
      case (saleNumber, quantity) =>
        for {
          user <- User.find(userId)
          sale <- Sale.findBy("saleNumber" -> saleNumber)
        } yield CartItem(user, sale, quantity.toInt)
    }
  }

  def count(userId: Long): Future[Int] = Future.successful {
    sedisPool.withJedisClient { client =>
      client.hgetAll(cartSedisKeyPrefix + userId).asScala.toSeq.map(_._2.toInt).sum
    }
  }

  def purge(userId: Long): Future[Int] = Future.successful {
    sedisPool.withJedisClient { client =>
      client.del(cartSedisKeyPrefix + userId)
      client.hgetAll(cartSedisKeyPrefix + userId).asScala.toSeq.map(_._2.toInt).sum
    }
  }

  def delete(userId: Long, saleNumberOpt: Option[String] = None): Future[Int] = Future.successful {
    sedisPool.withJedisClient { client =>
      saleNumberOpt match {
        case Some(saleNumber) => client.hdel(cartSedisKeyPrefix + userId, saleNumber)
        case None => client.del(cartSedisKeyPrefix + userId)
      }
      client.hgetAll(cartSedisKeyPrefix + userId).asScala.toSeq.map(_._2.toInt).sum
    }
  }

}
