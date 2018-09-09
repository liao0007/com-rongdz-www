package models.core

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class District(
                     override val id: Long,
                     var cityId: Int,
                     var name: String
                   ) extends ActiveRecord {
  lazy val city: ActiveRecord.BelongsToAssociation[District.this.type, City] = belongsTo[City]
}

object District extends ActiveRecordCompanion[District] with PlayFormSupport[District] {
  implicit val jsonFormat: OFormat[District] = Json.format[District]
}
