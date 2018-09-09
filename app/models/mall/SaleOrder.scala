package models.mall

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.user.User
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
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
                      var channel: String = SaleOrder.Channel.OnlineStore,
                      var deliveryType: String = SaleOrder.DeliveryType.Direct,
                      var carrier: Option[String] = None,
                      var trackingNumber: Option[String] = None,
                      var state: String = SaleOrder.State.Created,
                      var paymentOutTradeNumber: Option[String] = None,
                      var paymentMethod: String = SaleOrder.PaymentMethod.Wepay,
                      var paymentState: String = SaleOrder.PaymentState.Open,
                      var paymentTransactionId: Option[String] = None,
                      var shippingState: String = SaleOrder.ShippingState.Pending
                    ) extends ActiveRecord {
  lazy val user: ActiveRecord.BelongsToAssociation[SaleOrder.this.type, User] = belongsTo[User]
  lazy val details: ActiveRecord.HasManyAssociation[SaleOrder.this.type, SaleOrderDetail] = hasMany[SaleOrderDetail]
}

object SaleOrder extends ActiveRecordCompanion[SaleOrder] with PlayFormSupport[SaleOrder] {

  def newOrderNumber(userId: Long): String = {
    val pattern = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    pattern.print(DateTime.now()) + ("00000" + userId).takeRight(5)
  }

  sealed abstract class StateValue(val name: String) extends EnumAttributeValue

  object State extends EnumAttribute[StateValue] {

    case object Created extends StateValue("待处理")

    case object Audited extends StateValue("已处理")

    case object Disabled extends StateValue("已关闭")

    protected def all: Seq[StateValue] = Seq[StateValue](Created, Audited, Disabled)
  }

  sealed abstract class DeliveryTypeValue(val name: String) extends EnumAttributeValue

  object DeliveryType extends EnumAttribute[DeliveryTypeValue] {

    case object Direct extends DeliveryTypeValue("送货上门")

    case object Express extends DeliveryTypeValue("普通快递")

    case object Pickup extends DeliveryTypeValue("门店自取")

    protected def all: Seq[DeliveryTypeValue] = Seq[DeliveryTypeValue](Express, Direct, Pickup)
  }

  sealed abstract class PaymentMethodValue(val name: String) extends EnumAttributeValue

  object PaymentMethod extends EnumAttribute[PaymentMethodValue] {

    case object Alipay extends PaymentMethodValue("支付宝支付")

    case object Wepay extends PaymentMethodValue("微信支付")

    case object Cash extends PaymentMethodValue("现金支付")

    protected def all: Seq[PaymentMethodValue] = Seq(Wepay, Alipay, Cash)
  }

  sealed abstract class PaymentStateValue(val name: String) extends EnumAttributeValue

  object PaymentState extends EnumAttribute[PaymentStateValue] {

    case object Open extends PaymentStateValue("待付款")

    case object Paid extends PaymentStateValue("已付款")

    case object Closed extends PaymentStateValue("已关闭")

    case object Refunded extends PaymentStateValue("已退款")

    protected def all: Seq[PaymentStateValue] = Seq[PaymentStateValue](Open, Paid, Closed, Refunded)
  }

  sealed abstract class ShippingStateValue(val name: String) extends EnumAttributeValue

  object ShippingState extends EnumAttribute[ShippingStateValue] {

    case object Pending extends ShippingStateValue("待发货")

    case object Processing extends ShippingStateValue("正在发货")

    case object Shipped extends ShippingStateValue("已发货")

    case object Disabled extends ShippingStateValue("已关闭")

    protected def all: Seq[ShippingStateValue] = Seq[ShippingStateValue](Pending, Processing, Shipped, Disabled)
  }

  sealed abstract class ChannelValue(val name: String) extends EnumAttributeValue

  object Channel extends EnumAttribute[ChannelValue] {

    case object OnlineStore extends ChannelValue("网站")

    case object OnlineWechat extends ChannelValue("微信")

    case object OnlinePinan extends ChannelValue("平安")

    case object Offline extends ChannelValue("线下")

    case object Internal extends ChannelValue("内部")

    protected def all: Seq[ChannelValue] = Seq[ChannelValue](OnlineStore, OnlineWechat, OnlinePinan, Offline, Internal)
  }

  implicit val jsonFormat: OFormat[SaleOrder] = Json.format[SaleOrder]
}
