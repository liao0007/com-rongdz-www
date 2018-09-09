package models.product

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.mall.Sale
import play.api.libs.json.{Json, OFormat}

case class Product(
                    override val id: Long = 0L,
                    var brandId: Long,
                    var subcategoryId: Long,
                    var mku: String,
                    var name: String,
                    var attributeSetId: Long
                  ) extends ActiveRecord {
  lazy val brand: ActiveRecord.BelongsToAssociation[Product.this.type, Brand] = belongsTo[Brand]
  lazy val subcategory: ActiveRecord.BelongsToAssociation[Product.this.type, Subcategory] =
    belongsTo[Subcategory]
  lazy val attributeSet: ActiveRecord.BelongsToAssociation[Product.this.type, AttributeSet] =
    belongsTo[AttributeSet]
  lazy val skus: ActiveRecord.HasManyAssociation[Product.this.type, Sku] = hasMany[Sku]
  lazy val sales: ActiveRecord.HasManyAssociation[Product.this.type, Sale] = hasMany[Sale]
}

object Product extends ActiveRecordCompanion[Product] with PlayFormSupport[Product] {
  implicit val format: OFormat[Product] = Json.format[Product]
}
