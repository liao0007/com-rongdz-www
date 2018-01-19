package daos.default.mall

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class BookingFollowup(
    override val id: Long = 0L,
    var bookingId: Long,
    var description: String
) extends ActiveRecord {
  lazy val booking: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[BookingFollowup.this.type, Booking] = belongsTo[Booking]
}

object BookingFollowup extends ActiveRecordCompanion[BookingFollowup] with PlayFormSupport[BookingFollowup] {

  implicit val jsonFormat: OFormat[BookingFollowup] = Json.format[BookingFollowup]
}
