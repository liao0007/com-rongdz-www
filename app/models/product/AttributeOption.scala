package models.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class AttributeOption(
    override val id: Long = 0L,
    var attributeId: Long,
    var value: String,
    var name: String,
    var description: Option[String] = None,
    var image: Option[String] = None
) extends ActiveRecord {
  lazy val attribute: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[AttributeOption.this.type, Attribute] =
    belongsTo[Attribute]
}

object AttributeOption extends ActiveRecordCompanion[AttributeOption] with PlayFormSupport[AttributeOption] {
  implicit val format: OFormat[AttributeOption] = Json.format[AttributeOption]
}
