package models.core

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.core.AlipayLog.LogType
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
                      var logType: String = LogType.Query
                    ) extends ActiveRecord

object AlipayLog extends ActiveRecordCompanion[AlipayLog] with PlayFormSupport[AlipayLog] {

  sealed abstract class LogTypeValue(val name: String) extends EnumAttributeValue

  object LogType extends EnumAttribute[LogTypeValue] {

    case object Notify extends LogTypeValue("Notify")

    case object Query extends LogTypeValue("Query")

    case object Close extends LogTypeValue("Close")

    protected def all: Seq[LogTypeValue] = Seq[LogTypeValue](Notify, Query, Close)
  }

}
