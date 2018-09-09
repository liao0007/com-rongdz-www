package entities.mall

import models.mall.Sale
import models.user.User
import play.api.libs.json._

/**
  * Created by liangliao on 27/11/16.
  */
case class CartItem(user: User, sale: Sale, quantity: Int)

object CartItem {

  implicit val writes: Writes[CartItem] = Writes[CartItem] { cartItem =>
    val productSkuImage = for {
      productSku <- cartItem.sale.sku.toOption
    } yield productSku.imageUrls

    Json.obj(
      "userId" -> cartItem.user.id,
      "saleId" -> cartItem.sale.id,
      "quantity" -> cartItem.quantity,
      "productSkuImage" -> productSkuImage,
      "productTitle" -> cartItem.sale.title,
      "unitPrice" -> cartItem.sale.unitPrice
    )
  }

}
