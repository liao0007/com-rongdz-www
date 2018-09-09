package models.mall

import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.user.User
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, OFormat}

case class Booking(
                    override val id: Long = 0L,
                    var bookingNumber: String,
                    @Required(message = "姓名必填") var name: String,
                    @Required(message = "联系方式必填") @Format(value = """^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$""", message = "手机号码无效") var mobile: String,
                    @Required(message = "所在城市必填") var city: String,
                    var address: Option[String] = None,
                    var userId: Option[Long] = None,
                    var stylistId: Option[Long] = None,
                    var saleId: Option[Long] = None,
                    var districtId: Option[Long] = None,
                    var targetDateTime: Option[DateTime] = None,
                    var state: String = Booking.State.Open,
                    var channel: String = SaleOrder.Channel.OnlineStore,
                    var gender: String = User.Gender.Secret,
                    var servicePerson: Option[String] = None,
                    var memo: Option[String] = None
                  ) extends ActiveRecord {
  lazy val bookingFollowups: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Booking.this.type, BookingFollowup] =
    hasMany[BookingFollowup]
}

object Booking extends ActiveRecordCompanion[Booking] with PlayFormSupport[Booking] {

  def newBookingNumber: String = {
    val pattern = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS")
    pattern.print(DateTime.now())
  }

  sealed abstract class StateValue(val name: String) extends EnumAttributeValue

  object State extends EnumAttribute[StateValue] {

    case object Open extends StateValue("待处理")

    case object InProgress extends StateValue("已沟通")

    case object Pending extends StateValue("待上门")

    case object OrderCreated extends StateValue("成单")

    case object Closed extends StateValue("关闭")

    protected def all: Seq[StateValue] = Seq[StateValue](Open, InProgress, Pending, OrderCreated, Closed)
  }

  implicit val jsonFormat: OFormat[Booking] = Json.format[Booking]
}
