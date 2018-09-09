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

  abstract class InputType(val name: String) extends EnumAttributeValue

  object AttributeInputType extends EnumAttribute[InputType] {

    case object Text extends InputType("Text")

    case object Enu extends InputType("Enu")

    case object Bool extends InputType("Boolean")

    override protected def all: Seq[InputType] = Seq[InputType](Text, Enu, Bool)
  }

  implicit val format: OFormat[Attribute] = Json.format[Attribute]
}
