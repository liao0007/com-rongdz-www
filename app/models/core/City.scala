package models.core

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class City(
                 override val id: Long,
                 var provinceId: Int,
                 var name: String
               ) extends ActiveRecord {
  lazy val districts: ActiveRecord.HasManyAssociation[City.this.type, District] = hasMany[District]
  lazy val province: ActiveRecord.BelongsToAssociation[City.this.type, Province] = belongsTo[Province]
}

object City extends ActiveRecordCompanion[City] with PlayFormSupport[City] {
  implicit val jsonFormat: OFormat[City] = Json.format[City]
}
