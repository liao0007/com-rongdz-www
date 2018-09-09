package models.mall

import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.IterableAttribute
import models.{ActiveRecord, IterableAttribute}
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
    var state: String = BookingState.Open.toString,
    var channel: String = SaleOrderChannel.OnlineStore.toString,
    var gender: String = UserGender.Secret.toString,
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

  sealed class State(val name: String)
  object BookingState extends IterableAttribute[State] {
    case object Open         extends State("待处理")
    case object InProgress   extends State("已沟通")
    case object Pending      extends State("待上门")
    case object OrderCreated extends State("成单")
    case object Closed       extends State("关闭")
    protected def all: Seq[State]                     = Seq[State](Open, InProgress, Pending, OrderCreated, Closed)
    implicit def fromString(x: String): Option[State] = all.find(_.toString == x)
    implicit def toString(state: State): String       = state.toString
  }

  implicit val jsonFormat: OFormat[Booking] = Json.format[Booking]
}
