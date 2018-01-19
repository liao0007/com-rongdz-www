package daos.default.core

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.{ActiveRecord, IterableAttribute}
import daos.default.core.WepayLog.WepayLogType
import play.api.libs.json.{Json, OFormat}

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
    var logType: String = WepayLogType.Query
) extends ActiveRecord

object WepayLog extends ActiveRecordCompanion[WepayLog] with PlayFormSupport[WepayLog] {

  sealed trait LogType
  object WepayLogType extends IterableAttribute[LogType] {
    case object Notify extends LogType
    case object Query  extends LogType
    case object Close  extends LogType
    protected def all                                   = Seq[LogType](Notify, Query, Close)
    implicit def fromString(x: String): Option[LogType] = Seq[LogType](Notify, Query, Close).find(_.toString == x)
    implicit def toString(state: LogType): String       = state.toString
  }
}
