package models.core

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.core.AlipayLog.AlipayLogType
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import org.joda.time.DateTime

case class AlipayLog(
                      override val id: Long = 0L,
                      var notifyTime: DateTime,
                      var notifyType: String,
                      var notifyId: String,
                      var signType: String,
                      var sign: String,
                      var tradeNo: String,
                      var appId: String,
                      var outTradeNo: String,
                      var outBizNo: Option[String] = None,
                      var buyerId: Option[String] = None,
                      var buyerLogonId: Option[String] = None,
                      var sellerId: Option[String] = None,
                      var sellerEmail: Option[String] = None,
                      var tradeStatus: Option[String] = None,
                      var totalAmount: Option[Float] = None,
                      var receiptAmount: Option[Float] = None,
                      var invoiceAmount: Option[Float] = None,
                      var buyerPayAmount: Option[Float] = None,
                      var pointAmount: Option[Float] = None,
                      var refundFee: Option[Float] = None,
                      var sendBackFee: Option[Float] = None,
                      var subject: Option[String] = None,
                      var body: Option[String] = None,
                      var gmtCreate: Option[DateTime] = None,
                      var gmtPayment: Option[DateTime] = None,
                      var gmtRefund: Option[DateTime] = None,
                      var gmtClose: Option[DateTime] = None,
                      var fundBillList: Option[String] = None,
                      var logType: String = AlipayLogType.Query
                    ) extends ActiveRecord

object AlipayLog extends ActiveRecordCompanion[AlipayLog] with PlayFormSupport[AlipayLog] {

  sealed class LogType(val name: String) extends EnumAttributeValue

  object AlipayLogType extends EnumAttribute[LogType] {

    case object Notify extends LogType("Notify")

    case object Query extends LogType("Query")

    case object Close extends LogType("Close")

    protected def all: Seq[LogType] = Seq[LogType](Notify, Query, Close)
  }

}
