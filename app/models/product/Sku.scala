package models.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.mall.Sale
import play.api.libs.json.{Json, OFormat}

case class Sku(
    override val id: Long = 0L,
    var productId: Long,
    var attributeValueSetId: Long,
    var sku: String,
    var title: String,
    var description: String,
    var detail: Option[String],
    var image: String,
    var hoverImage: Option[String],
    var unitPrice: Float
) extends ActiveRecord {
  lazy val product: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Sku.this.type, Product] = belongsTo[Product]
  lazy val sales: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Sku.this.type, Sale]        = hasMany[Sale]
  lazy val attributeValueSet: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Sku.this.type, AttributeValueSet] =
    belongsTo[AttributeValueSet]
  lazy val imageUrls: Seq[String] = {
    val imageUrls = image.split(",").toSeq.map(_.trim)
    if (imageUrls.nonEmpty) {
      imageUrls
    } else {
      Seq("default image url")
    }
  }
}

object Sku extends ActiveRecordCompanion[Sku] with PlayFormSupport[Sku] {
  implicit val format: OFormat[Sku] = Json.format[Sku]
}
