package daos.default.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import com.github.aselab.activerecord.dsl._
import daos.ActiveRecord
import daos.default.mall.Sale
import play.api.libs.json.{Json, OFormat}

case class Product(
    override val id: Long = 0L,
    var brandId: Long,
    var subcategoryId: Long,
    var mku: String,
    var name: String,
    var attributeSetId: Long
) extends ActiveRecord {
  lazy val brand: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Product.this.type, Brand] = belongsTo[Brand]
  lazy val subcategory: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Product.this.type, Subcategory] =
    belongsTo[Subcategory]
  lazy val attributeSet: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Product.this.type, AttributeSet] =
    belongsTo[AttributeSet]
  lazy val skus: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Product.this.type, Sku]   = hasMany[Sku]
  lazy val sales: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Product.this.type, Sale] = hasMany[Sale]
}

object Product extends ActiveRecordCompanion[Product] with PlayFormSupport[Product] {
  implicit val format: OFormat[Product] = Json.format[Product]
}
