package daos.default.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import daos.default.product.Attribute.AttributeInputType
import play.api.libs.json.{Json, OFormat}
import com.github.aselab.activerecord.dsl._

case class AttributeValue(
    override val id: Long = 0L,
    var attributeValueSetId: Long,
    var attributeId: Long,
    var value: Option[String] = None
) extends ActiveRecord {
  lazy val attributeValueSet: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[AttributeValue.this.type, AttributeValueSet] =
    belongsTo[AttributeValueSet]
  lazy val attribute: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[AttributeValue.this.type, Attribute] =
    belongsTo[Attribute]

  def readableValue(showOptionValue: Boolean = false): Option[String] = value flatMap { v =>
    AttributeInputType.fromString(attribute.inputType) match {
      case Some(Attribute.AttributeInputType.Enu) =>
        AttributeOption.find(v.toLong).map { attributeOption =>
          if (showOptionValue) { attributeOption.name + " - " + attributeOption.value } else {
            attributeOption.name
          }
        }
      case _ => Some(v)
    }
  }

}

object AttributeValue extends ActiveRecordCompanion[AttributeValue] with PlayFormSupport[AttributeValue] {
  implicit val format: OFormat[AttributeValue] = Json.format[AttributeValue]
}
