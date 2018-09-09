package models.product

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class Brand(
                  override val id: Long = 0L,
                  var name: String
                ) extends ActiveRecord {
  lazy val products: ActiveRecord.HasManyAssociation[Brand.this.type, Product] = hasMany[Product]
}

object Brand extends ActiveRecordCompanion[Brand] with PlayFormSupport[Brand] {
  implicit val format: OFormat[Brand] = Json.format[Brand]
}
