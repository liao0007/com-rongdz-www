package controllers.alipay

import javax.inject.Inject

import auth.JWTEnv
import com.alipay.api.AlipayConstants
import com.alipay.api.internal.util.AlipaySignature
import com.alipay.api.internal.util.codec.Base64
import com.mohiva.play.silhouette.api.Silhouette
import daos.default.core.AlipayLog
import daos.default.core.AlipayLog.AlipayLogType
import daos.default.mall.SaleOrder
import daos.default.mall.SaleOrder.{SaleOrderPaymentMethod, SaleOrderPaymentState}
import org.joda.time.format.DateTimeFormat
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import services.AlipayService
import services.mall.SaleOrderService

import collection.JavaConverters._
import scala.concurrent.Future

/**
  * Created by liangliao on 7/14/16.
  */
class ApplicationController @Inject()(val messagesApi: MessagesApi,
                                      silhouette: Silhouette[JWTEnv],
                                      saleOrderService: SaleOrderService,
                                      configuration: Configuration,
                                      alipayService: AlipayService) extends Controller {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("AlipayRoutes")(
        controllers.wechat.routes.javascript.AccountController.signIn
      )
    ).as("text/javascript")
  }

  def index = Action { implicit request =>
    NotImplemented
  }

  def payNotify: Action[AnyContent] = Action.async { implicit request =>
    val parser = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    val map = request.body.asFormUrlEncoded.get.map { case(key, seq ) =>
      key -> seq.head
    }

    val signType = map("sign_type")

    //validate rsa
    if (AlipaySignature.rsaCheckV1(collection.mutable.Map(map.toSeq: _*).asJava, configuration.underlying.getString("alipay.alipayPublicKey"), AlipayConstants.CHARSET_UTF8, signType)) {

      AlipayLog.findBy("notifyId" -> map("notify_id")) match {
        case None =>
          val alipayLog = AlipayLog(
            notifyTime = map.get("notify_time").map(parser.parseDateTime).get,
            notifyType = map("notify_type"),
            notifyId = map("notify_id"),
            signType = map("sign_type"),
            sign = map("sign"),
            tradeNo = map("trade_no"),
            appId = map("app_id"),
            outTradeNo = map("out_trade_no"),
            outBizNo = map.get("out_biz_no"),
            buyerId = map.get("buyer_id"),
            buyerLogonId = map.get("buyer_logon_id"),
            sellerId = map.get("seller_id"),
            sellerEmail = map.get("seller_email"),
            tradeStatus = map.get("trade_status"),
            totalAmount = map.get("total_amount").map(_.toFloat),
            receiptAmount = map.get("receipt_amount").map(_.toFloat),
            invoiceAmount = map.get("invoice_amount").map(_.toFloat),
            buyerPayAmount = map.get("buyer_pay_amount").map(_.toFloat),
            pointAmount = map.get("point_amount").map(_.toFloat),
            refundFee = map.get("refund_fee").map(_.toFloat),
            sendBackFee = map.get("send_back_fee").map(_.toFloat),
            subject = map.get("subject"),
            body = map.get("map"),
            gmtCreate = map.get("gmt_create").map(parser.parseDateTime),
            gmtPayment = map.get("gmt_payment").map(parser.parseDateTime),
            gmtRefund = map.get("gmt_refund").map(parser.parseDateTime),
            gmtClose = map.get("gmt_close").map(parser.parseDateTime),
            fundBillList = map.get("fund_bill_list"),
            logType = AlipayLogType.Notify
          ).create

          if (alipayLog.tradeStatus.contains("TRADE_SUCCESS")) {
            SaleOrder.findBy("paymentOutTradeNumber" -> alipayLog.outTradeNo) match {
              case Some(saleOrder: SaleOrder) if alipayLog.totalAmount.contains(saleOrder.totalAmount) =>
                saleOrder.paymentState = SaleOrderPaymentState.Paid
                saleOrder.paymentMethod = SaleOrderPaymentMethod.Wepay
                saleOrder.paymentTransactionId = Some(alipayLog.tradeNo)
                saleOrder.update
              case _ =>
              //alert to check inconsistency
            }
          }

        case _ =>

      }

      Future.successful {
        Ok("success")
      }
    } else {
      Future.successful {
        NotFound
      }
    }
  }

}
