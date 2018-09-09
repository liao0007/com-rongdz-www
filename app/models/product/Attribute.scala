package models.product

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Json, OFormat}

case class Attribute(
                      override val id: Long = 0L,
                      var name: String,
                      var description: Option[String],
                      var inputType: String
                    ) extends ActiveRecord {
  lazy val attributeSetDetails: ActiveRecord.HasManyAssociation[Attribute.this.type, AttributeSetDetail] = hasMany[AttributeSetDetail]
  lazy val attributeOptions: ActiveRecord.HasManyAssociation[Attribute.this.type, AttributeOption] = hasMany[AttributeOption]
}

object Attribute extends ActiveRecordCompanion[Attribute] with PlayFormSupport[Attribute] {

  sealed abstract class InputTypeValue(val name: String) extends EnumAttributeValue

  object InputType extends EnumAttribute[InputTypeValue] {

    case object Text extends InputTypeValue("Text")

    case object Enu extends InputTypeValue("Enu")

    case object Bool extends InputTypeValue("Boolean")

    override protected def all: Seq[InputTypeValue] = Seq[InputTypeValue](Text, Enu, Bool)
  }

  implicit val format: OFormat[Attribute] = Json.format[Attribute]
}
