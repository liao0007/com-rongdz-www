package models.mall

import models.user.User
import play.api.libs.json._

/**
  * Created by liangliao on 27/11/16.
  */
case class CartItem(user: User, sale: Sale, quantity: Int)

object CartItem {

  implicit val cartWrites = new Writes[CartItem] {

    def writes(cart: CartItem): JsObject = {

      val productSkuImage = for {
        productSku <- cart.sale.sku.toOption
      } yield productSku.imageUrls

      Json.obj(
        "userId" -> cart.user.id,
        "saleId" -> cart.sale.id,
        "quantity" -> cart.quantity,
        "productSkuImage" -> productSkuImage,
        "productTitle" -> cart.sale.title,
        "unitPrice" -> cart.sale.unitPrice
      )
    }
  }

}
