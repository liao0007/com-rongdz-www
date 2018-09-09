package controllers.rest

import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.Silhouette
import controllers.customRoutes
import javax.inject.Inject
import models.mall.Sale
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.mall.CartService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CartController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[JWTEnv], cartService: CartService)
    extends Controller
    with I18nSupport {

  def update(saleNumber: String, quantity: Int): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    if (quantity == 0) {
      cartService.delete(request.identity.id, Some(saleNumber)) map { totalQuantity =>
        Ok(totalQuantity.toString)
      }
    } else {
      cartService.update(request.identity.id, saleNumber, quantity) map { totalQuantity =>
        Ok(totalQuantity.toString)
      }
    }
  }

  def count(): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) =>
        cartService.count(user.id) map { totalQuantity =>
          Ok(totalQuantity.toString)
        }
      case _ => Future.successful(Ok("0"))
    }
  }

  def miniView(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    cartService.list(request.identity.id) map { cartItems =>
      if (cartItems.isEmpty) {
        Ok("<div>您的购物袋是空的</div>")
      } else {
        val items = cartItems map { cartItem =>
          s"""
             |<div class="clearfix mb-2 row">
             |		<img class="col-xs-4 pr-0" src="${customRoutes.UploadedAssets
               .at(cartItem.sale.sku.imageUrls(0))}?x-oss-process=image/resize,limit_0,w_160/sharpen,100"/>
             |		<div class="col-xs-8">
             |			<h3 class="mb-0">${cartItem.sale.title}</h3>
             |			<div class="text-muted text-uppercase">${cartItem.sale.sku.sku}</div>
             |			数量: ${cartItem.quantity} 件<br />
             |			小计: ¥${cartItem.sale.originalUnitPrice * cartItem.quantity}<br />
             |		</div>
             |	</div>
          """.stripMargin
        } mkString ""

        val subtotal = s"""
                          |<div class="clearfix" style="font-size: 1.2em; margin-bottom: 36px;">
                          |		<div class="float-md-right"><strong>总价: ¥${cartItems
                            .map(cartItem => cartItem.sale.originalUnitPrice * cartItem.quantity)
                            .sum}</strong></div>
                          |</div>
                          |<div class="clearfix">
                          | 	<a href="${controllers.store.routes.ApplicationController.bag()}" class="cta-button alt">查看购物袋</a>
                          | 	<a href="${controllers.store.routes.ApplicationController.checkout()}" class="cta-button ml-1">立即结算</a>
                          |</div>
          """.stripMargin

        val result = s"""
                        |<div>
                        |$items
                        |$subtotal
                        |</div>
          """.stripMargin

        Ok(result)
      }

    }
  }

  def added(saleNumber: String, quantity: Int): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      Sale.findBy("saleNumber" -> saleNumber) match {
        case Some(sale) =>
          Ok(
            s"""
               |<div>
               |	<h2 class="text-xs-center mb-2">成功加入购物袋</h2>
               |	<div class="clearfix mb-2">
               |		<img class="col-xs-4 pr-0" src="${customRoutes.UploadedAssets
                 .at(sale.sku.imageUrls(0))}?x-oss-process=image/resize,limit_0,w_160/sharpen,100"/>
               |		<div class="col-xs-8">
               |			<h3 class="mb-0">${sale.title}</h3>
               |			<div class="text-muted text-uppercase">${sale.sku.sku}</div>
               |			数量: ${quantity} 件<br />
               |			单价: ¥${sale.originalUnitPrice}<br />
               |		</div>
               |	</div>
               |	<a href="${controllers.store.routes.ApplicationController.bag()}" class="cta-button">查看购物袋</a>
               |</div>
        """.stripMargin
          )
        case _ => NotFound
      }
    }
  }

  def subtotal(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    cartService.list(request.identity.id) map { cartItems =>
      val subtotal = cartItems.map(cartItem => cartItem.sale.originalUnitPrice * cartItem.quantity).sum
      Ok(s"""
           |<div>
           |	<div class="subtotal-box">
           |		<table class="table subtotal-table">
           |			<tbody>
           |				<tr>
           |					<td>商品金额</td>
           |					<td class="text-xs-right">¥${subtotal}</td>
           |				</tr>
           |				<tr>
           |					<td>运费 <i class="icon-info-circled popover-trigger popover-trigger-text" data-content="默认顺丰快递，4-6天发货"></i></td>
           |					<td class="text-xs-right">¥0.00</td>
           |				</tr>
           |				<tr class="total-row font-weight-bold">
           |					<td class="text-uppercase">应付总额</td>
           |					<td class="text-xs-right">¥${subtotal}</td>
           |				</tr>
           |			</tbody>
           |		</table>
           |	</div>
           |</div>
         """.stripMargin)
    }
  }

}
