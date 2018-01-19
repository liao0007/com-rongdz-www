package controllers.rest

import javax.inject.Inject

import auth.JWTEnv
import com.mohiva.play.silhouette.api.Silhouette
import daos.default.mall.SaleOrder
import daos.default.mall.SaleOrder.SaleOrderPaymentMethod
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{AlipayService, WxMpService}
import services.mall.SaleOrderService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SaleOrderController @Inject()(val messagesApi: MessagesApi,
                                    silhouette: Silhouette[JWTEnv],
                                    saleOrderService: SaleOrderService,
                                    wxMpService: WxMpService,
                                    alipayService: AlipayService)
    extends Controller
    with ResponseSupport {

  def create: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { implicit request =>
    request.body
      .validate[SaleOrderCreateRequest]
      .map { saleOrderCreateRequest =>
        saleOrderService.create(request.identity.id, saleOrderCreateRequest) map { createdSaleOrder =>
          Created(Json.toJson(createdSaleOrder))
        }
      }
      .recoverTotal(badRequestWithMessage)

  }

  def wepayInfo(orderNumber: String, tradeType: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      val notifyUrl = controllers.wechat.routes.ApplicationController.payNotify().absoluteURL()
      saleOrderService.wepayInfo(orderNumber, notifyUrl, tradeType) match {
        case Some(payInfo) => Ok(Json.toJson(payInfo))
        case _             => Conflict(Json.toJson(Bad("invalid.payment.state")))
      }
    }
  }

  def alipayInfo(orderNumber: String, tradeType: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      val notifyUrl = controllers.alipay.routes.ApplicationController.payNotify().absoluteURL()
      saleOrderService.alipayInfo(orderNumber, notifyUrl, tradeType) match {
        case Some(payInfo) => Ok(payInfo)
        case _             => Conflict(Json.toJson(Bad("invalid.payment.state")))
      }
    }
  }

  def cashInfo(orderNumber: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      SaleOrder.findBy("orderNumber" -> orderNumber) match {
        case Some(saleOrder: SaleOrder) =>
          saleOrder.paymentMethod = SaleOrderPaymentMethod.Cash
          saleOrder.update
          Ok
        case _ => NotFound
      }
    }
  }

  def paymentState(orderNumber: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    SaleOrder.findBy("orderNumber" -> orderNumber, "userId" -> request.identity.id) match {
      case Some(saleOrder: SaleOrder) => Future.successful(Ok(Json.toJson(saleOrder)))
      case _                          => Future.successful(NotFound)
    }
  }

}
