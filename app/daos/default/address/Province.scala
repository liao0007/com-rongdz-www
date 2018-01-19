package daos.default.address

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class Province(
    override val id: Long,
    var name: String
) extends ActiveRecord {
  lazy val cities: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[
    Province.this.type,
    City] = hasMany[City]
}

object Province
    extends ActiveRecordCompanion[Province]
    with PlayFormSupport[Province] {
  implicit val jsonFormat: OFormat[Province] = Json.format[Province]
}
