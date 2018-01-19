package daos.default.address

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class District(
    override val id: Long,
    var cityId: Int,
    var name: String
) extends ActiveRecord {
  lazy val city: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[
    District.this.type,
    City] =
    belongsTo[City]
}

object District
    extends ActiveRecordCompanion[District]
    with PlayFormSupport[District] {
  implicit val jsonFormat: OFormat[District] = Json.format[District]
}
