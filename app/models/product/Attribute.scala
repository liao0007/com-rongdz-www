package models.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class Attribute(
    override val id: Long = 0L,
    var name: String,
    var description: Option[String],
    var inputType: String
) extends ActiveRecord {
  lazy val attributeSetDetails: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Attribute.this.type, AttributeSetDetail] =
    hasMany[AttributeSetDetail]
  lazy val attributeOptions: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Attribute.this.type, AttributeOption] =
    hasMany[AttributeOption]
}

object Attribute extends ActiveRecordCompanion[Attribute] with PlayFormSupport[Attribute] {

  abstract class InputType(val name: String)
  object AttributeInputType extends IterableAttribute[InputType] {
    case object Text extends InputType("自由文本")
    case object Enu  extends InputType("枚举")
    case object Bool extends InputType("布尔")

    implicit def fromString(x: String): Option[InputType] = all.find(_.toString == x)
    implicit def toString(state: InputType): String       = state.toString

    override protected def all: Seq[InputType] = Seq[InputType](Text, Enu, Bool)
  }

  implicit val format: OFormat[Attribute] = Json.format[Attribute]
}
