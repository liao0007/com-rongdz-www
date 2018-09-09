package models.product

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class AttributeSet(
                         override val id: Long = 0L,
                         var name: String,
                         var description: Option[String] = None
                       ) extends ActiveRecord {
  lazy val products: ActiveRecord.HasManyAssociation[AttributeSet.this.type, Product] = hasMany[Product]
  lazy val attributeValueSets: ActiveRecord.HasManyAssociation[AttributeSet.this.type, AttributeValueSet] = hasMany[AttributeValueSet]
  lazy val attributeSetDetails: ActiveRecord.HasManyAssociation[AttributeSet.this.type, AttributeSetDetail] = hasMany[AttributeSetDetail]
}

object AttributeSet extends ActiveRecordCompanion[AttributeSet] with PlayFormSupport[AttributeSet] {
  implicit val format: OFormat[AttributeSet] = Json.format[AttributeSet]
}
