package models.product

import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.product.Attribute.InputType
import play.api.libs.json.{Json, OFormat}

case class AttributeValue(
                           override val id: Long = 0L,
                           var attributeValueSetId: Long,
                           var attributeId: Long,
                           var value: Option[String] = None
                         ) extends ActiveRecord {
  lazy val attributeValueSet: ActiveRecord.BelongsToAssociation[AttributeValue.this.type, AttributeValueSet] = belongsTo[AttributeValueSet]
  lazy val attribute: ActiveRecord.BelongsToAssociation[AttributeValue.this.type, Attribute] = belongsTo[Attribute]

  def readableValue(showOptionValue: Boolean = false): Option[String] = value flatMap { v =>
    InputType.fromString(attribute.inputType) match {
      case Some(Attribute.InputType.Enu) =>
        AttributeOption.find(v.toLong).map { attributeOption =>
          if (showOptionValue) {
            attributeOption.name + " - " + attributeOption.value
          } else {
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
