package models.mall

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.user.User
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, OFormat}

case class SaleOrder(
    override val id: Long = 0L,
    var orderNumber: String,
    var userId: Long,
    var totalAmount: Float,
    var shipToAddressId: Long,
    var shipToName: String,
    var shipToProvince: String,
    var shipToCity: String,
    var shipToDistrict: String,
    var shipToMobile: String,
    var shipToAddress: String,
    var memo: Option[String] = None,
    var channel: String = SaleOrderChannel.OnlineStore,
    var deliveryType: String = SaleOrderDeliveryType.Direct,
    var carrier: Option[String] = None,
    var trackingNumber: Option[String] = None,
    var state: String = SaleOrderState.Created,
    var paymentOutTradeNumber: Option[String] = None,
    var paymentMethod: String = SaleOrderPaymentMethod.Wepay,
    var paymentState: String = SaleOrderPaymentState.Open,
    var paymentTransactionId: Option[String] = None,
    var shippingState: String = SaleOrderShippingState.Pending
) extends ActiveRecord {
  lazy val user: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[SaleOrder.this.type, User] = belongsTo[User]
  lazy val details: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[SaleOrder.this.type, SaleOrderDetail] =
    hasMany[SaleOrderDetail]
}

object SaleOrder extends ActiveRecordCompanion[SaleOrder] with PlayFormSupport[SaleOrder] {

  def newOrderNumber(userId: Long): String = {
    val pattern = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    pattern.print(DateTime.now()) + ("00000" + userId).takeRight(5)
  }

  sealed class State(val name: String)
  object SaleOrderState extends EnumAttribute[State] {
    case object Created  extends State("待处理")
    case object Audited  extends State("已处理")
    case object Disabled extends State("已关闭")

    protected def all: Seq[State] = Seq[State](Created, Audited, Disabled)

    implicit def fromString(x: String): Option[State] = all.find(_.toString == x)
    implicit def toString(state: State): String       = state.toString
  }

  sealed class DeliveryType(val name: String)
  object SaleOrderDeliveryType extends EnumAttribute[DeliveryType] {
    case object Direct  extends DeliveryType("送货上门")
    case object Express extends DeliveryType("普通快递")
    case object Pickup  extends DeliveryType("门店自取")

    protected def all: Seq[DeliveryType] = Seq[DeliveryType](Express, Direct, Pickup)

    implicit def fromString(x: String): Option[DeliveryType] = all.find(_.toString == x)
    implicit def toString(s: DeliveryType): String           = s.toString
  }

  sealed class PaymentMethod(val name: String)
  object SaleOrderPaymentMethod extends EnumAttribute[PaymentMethod] {
    case object Alipay extends PaymentMethod("支付宝支付")
    case object Wepay  extends PaymentMethod("微信支付")
    case object Cash   extends PaymentMethod("现金支付")

    protected def all: Seq[PaymentMethod] = Seq(Wepay, Alipay, Cash)

    implicit def fromString(x: String): Option[PaymentMethod] = all.find(_.toString == x)
    implicit def toString(s: PaymentMethod): String           = s.toString
  }

  sealed class PaymentState(val name: String)
  object SaleOrderPaymentState extends EnumAttribute[PaymentState] {
    case object Open     extends PaymentState("待付款")
    case object Paid     extends PaymentState("已付款")
    case object Closed   extends PaymentState("已关闭")
    case object Refunded extends PaymentState("已退款")

    protected def all: Seq[PaymentState] = Seq[PaymentState](Open, Paid, Closed, Refunded)

    implicit def fromString(x: String): Option[PaymentState] = all.find(_.toString == x)
    implicit def toString(s: PaymentState): String           = s.toString
  }

  sealed class ShippingState(val name: String)
  object SaleOrderShippingState extends EnumAttribute[ShippingState] {
    case object Pending    extends ShippingState("待发货")
    case object Processing extends ShippingState("正在发货")
    case object Shipped    extends ShippingState("已发货")
    case object Disabled   extends ShippingState("已关闭")

    protected def all: Seq[ShippingState] = Seq[ShippingState](Pending, Processing, Shipped, Disabled)

    implicit def fromString(x: String): Option[ShippingState] = all.find(_.toString == x)
    implicit def toString(s: ShippingState): String           = s.toString
  }

  sealed class Channel(val name: String)
  object SaleOrderChannel extends EnumAttribute[Channel] {
    case object OnlineStore  extends Channel("网站")
    case object OnlineWechat extends Channel("微信")
    case object OnlinePinan  extends Channel("平安")
    case object Offline      extends Channel("线下")
    case object Internal     extends Channel("内部")

    protected def all: Seq[Channel] = Seq[Channel](OnlineStore, OnlineWechat, OnlinePinan, Offline, Internal)

    implicit def fromString(x: String): Option[Channel] = all.find(_.toString == x)
    implicit def toString(s: Channel): String           = s.toString
  }

  implicit val jsonFormat: OFormat[SaleOrder] = Json.format[SaleOrder]
}
