package daos.default.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class Brand(
                  override val id: Long = 0L,
    var name: String
) extends ActiveRecord {
  lazy val products: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Brand.this.type, Product] = hasMany[Product]
}

object Brand extends ActiveRecordCompanion[Brand] with PlayFormSupport[Brand] {
  implicit val format: OFormat[Brand] = Json.format[Brand]
}
