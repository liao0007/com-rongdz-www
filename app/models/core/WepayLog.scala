package models.core

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.core.WepayLog.LogType
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}

case class WepayLog(
                     override val id: Long = 0L,
                     var returnCode: String,
                     var returnMsg: Option[String] = None,
                     var appid: String,
                     var mchId: String,
                     var deviceInfo: Option[String] = None,
                     var nonceStr: String,
                     var sign: String,
                     var signType: Option[String] = Some("MD5"),
                     var resultCode: String,
                     var errCode: Option[String] = None,
                     var errCodeDes: Option[String] = None,
                     var openid: String,
                     var isSubscribe: Option[String] = None,
                     var tradeType: String,
                     var bankType: String,
                     var totalFee: Option[Float] = None,
                     var feeType: Option[String] = None,
                     var cashFee: Float,
                     var cashFeeType: Option[String] = None,
                     var couponFee: Option[Float] = None,
                     var couponCount: Option[Int] = None,
                     var transactionId: String,
                     var outTradeNo: String,
                     var attach: Option[String] = None,
                     var timeEnd: String,
                     var logType: String = LogType.Query
                   ) extends ActiveRecord

object WepayLog extends ActiveRecordCompanion[WepayLog] with PlayFormSupport[WepayLog] {

  sealed abstract class LogTypeValue(val name: String) extends EnumAttributeValue

  object LogType extends EnumAttribute[LogTypeValue] {

    case object Notify extends LogTypeValue("Notify")

    case object Query extends LogTypeValue("Query")

    case object Close extends LogTypeValue("Close")

    protected def all: Seq[LogTypeValue] = Seq[LogTypeValue](Notify, Query, Close)
  }

}
