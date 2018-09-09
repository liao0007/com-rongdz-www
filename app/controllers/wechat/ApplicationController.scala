package controllers.wechat

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import models.core.WepayLog
import models.mall.SaleOrder
import play.api.i18n.MessagesApi
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import services.WxMpService
import services.mall.SaleOrderService

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.xml.NodeSeq

/**
  * Created by liangliao on 7/14/16.
  */
class ApplicationController @Inject()(val messagesApi: MessagesApi,
                                      silhouette: Silhouette[JWTEnv],
                                      saleOrderService: SaleOrderService,
                                      wxMpService: WxMpService) extends Controller {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("WechatRoutes")(
        controllers.wechat.routes.javascript.ApplicationController.jsApiSignature,
        controllers.wechat.routes.javascript.ApplicationController.payNotify,
        controllers.wechat.routes.javascript.AccountController.signIn
      )
    ).as("text/javascript")
  }


  case class JsApiSignature(appId: String, nonceStr: String, timestamp: Long, url: String, signature: String)
  object JsApiSignature {
    implicit val format: OFormat[JsApiSignature] = Json.format[JsApiSignature]
  }
  def jsApiSignature: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      val jsApiSignature = wxMpService.createJsapiSignature(request.headers("referer"));
      Ok(
        Json.toJson(
          JsApiSignature(
            appId = jsApiSignature.getAppid,
            nonceStr = jsApiSignature.getNoncestr,
            timestamp = jsApiSignature.getTimestamp,
            url = jsApiSignature.getUrl,
            signature = jsApiSignature.getSignature
          )))
    }
  }

  def payNotify: Action[NodeSeq] = Action.async(parse.xml) { implicit request =>
    val wxMpPayService = wxMpService.getPayService

    val node = scala.xml.Utility.trim(request.body.head)
    val sign = (node \ "sign").text
    val map = (node.child.filterNot(_.label == "sign") map { node =>
      node.label -> node.text
    }).toMap

    if (wxMpPayService.checkJSSDKCallbackDataSignature(collection.mutable.Map(map.toSeq: _*).asJava, sign)) {
      //valid
      val wxPayJsSDKCallback: WxPayJsSDKCallback = wxMpPayService.getJSSDKCallbackData(request.body.toString)

      WepayLog.findBy("transactionId" -> wxPayJsSDKCallback.getTransaction_id) match {
        case None =>
          val wechatPayLog = WepayLog(
            returnCode = wxPayJsSDKCallback.getReturn_code,
            returnMsg = Option(wxPayJsSDKCallback.getReturn_msg),
            appid = wxPayJsSDKCallback.getAppid,
            mchId = wxPayJsSDKCallback.getMch_id,
            deviceInfo = Option(wxPayJsSDKCallback.getDevice_info),
            nonceStr = wxPayJsSDKCallback.getNonce_str,
            sign = wxPayJsSDKCallback.getSign,
            resultCode = wxPayJsSDKCallback.getResult_code,
            errCode = Option(wxPayJsSDKCallback.getErr_code),
            errCodeDes = Option(wxPayJsSDKCallback.getErr_code_des),
            openid = wxPayJsSDKCallback.getOpenid,
            isSubscribe = Option(wxPayJsSDKCallback.getIs_subscribe),
            tradeType = wxPayJsSDKCallback.getTrade_type,
            bankType = wxPayJsSDKCallback.getBank_type,
            totalFee = Option(wxPayJsSDKCallback.getTotal_fee).map(_.toFloat),
            feeType = Option(wxPayJsSDKCallback.getFee_type),
            cashFee = wxPayJsSDKCallback.getCash_fee.toFloat,
            cashFeeType = Option(wxPayJsSDKCallback.getCash_fee_type),
            couponFee = Option(wxPayJsSDKCallback.getCoupon_fee).map(_.toFloat),
            couponCount = Option(wxPayJsSDKCallback.getCoupon_count).map(_.toInt),
            transactionId = wxPayJsSDKCallback.getTransaction_id,
            outTradeNo = wxPayJsSDKCallback.getOut_trade_no,
            attach = Option(wxPayJsSDKCallback.getAttach),
            timeEnd = wxPayJsSDKCallback.getTime_end,
            logType = WepayLogType.Notify
          ).create

          if (wechatPayLog.returnCode == "SUCCESS" && wechatPayLog.resultCode == "SUCCESS") {
            SaleOrder.findBy("paymentOutTradeNumber" -> wechatPayLog.outTradeNo) match {
              case Some(saleOrder: SaleOrder) if Math.abs(wechatPayLog.totalFee.get / 100 - saleOrder.totalAmount ) < 0.01 =>
                saleOrder.paymentState = SaleOrderPaymentState.Paid
                saleOrder.paymentMethod = SaleOrderPaymentMethod.Wepay
                saleOrder.paymentTransactionId = Some(wechatPayLog.transactionId)
                saleOrder.update
              case _ =>
                //alert to check inconsistency
            }
          }
        case _ =>
      }

      Future.successful {
        Ok(<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>)
      }
    } else {
      //invalid signature
      Future.successful { Unauthorized }
    }
  }

}
