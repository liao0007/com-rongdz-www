package models.core

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class Province(
                     override val id: Long,
                     var name: String
                   ) extends ActiveRecord {
  lazy val cities: ActiveRecord.HasManyAssociation[Province.this.type, City] = hasMany[City]
}

object Province extends ActiveRecordCompanion[Province] with PlayFormSupport[Province] {
  implicit val jsonFormat: OFormat[Province] = Json.format[Province]
}
