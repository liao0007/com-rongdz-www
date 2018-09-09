package models.mall

import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.product.Attribute.InputType
import models.product.{Attribute, AttributeOption}
import play.api.libs.json.{Json, OFormat}

case class SaleOrderDetailAttributeValue(
                                          override val id: Long = 0L,
                                          var saleOrderDetailId: Long,
                                          var attributeId: Long,
                                          var value: Option[String] = None
                                        ) extends ActiveRecord {
  lazy val saleOrderDetail: ActiveRecord.BelongsToAssociation[SaleOrderDetailAttributeValue.this.type, SaleOrderDetail] = belongsTo[SaleOrderDetail]
  lazy val attribute: ActiveRecord.BelongsToAssociation[SaleOrderDetailAttributeValue.this.type, Attribute] = belongsTo[Attribute]

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

object SaleOrderDetailAttributeValue extends ActiveRecordCompanion[SaleOrderDetailAttributeValue] with PlayFormSupport[SaleOrderDetailAttributeValue] {
  implicit val format: OFormat[SaleOrderDetailAttributeValue] = Json.format[SaleOrderDetailAttributeValue]
}
