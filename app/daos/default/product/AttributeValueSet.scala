package daos.default.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import com.github.aselab.activerecord.dsl._
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class AttributeValueSet(
    override val id: Long = 0L,
    var attributeSetId: Long,
    var name: String,
    var description: Option[String] = None
) extends ActiveRecord {
  lazy val attributeSet: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[AttributeValueSet.this.type, AttributeSet] =
    belongsTo[AttributeSet]
  lazy val attributeValues: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[AttributeValueSet.this.type, AttributeValue] =
    hasMany[AttributeValue]
}

object AttributeValueSet extends ActiveRecordCompanion[AttributeValueSet] with PlayFormSupport[AttributeValueSet] {
  implicit val format: OFormat[AttributeValueSet] = Json.format[AttributeValueSet]
}
