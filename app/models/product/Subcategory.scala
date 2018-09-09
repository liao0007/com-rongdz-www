package models.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.mall.Sale
import play.api.libs.json.{Json, OFormat}

case class Subcategory(
    override val id: Long = 0L,
    var categoryId: Long,
    var name: String,
    var description: String,
    var sequence: Option[Int],
    var bannerImage: Option[String] = None,
    var guideLink: Option[String] = None
) extends ActiveRecord {
  lazy val category: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Subcategory.this.type, Category] = belongsTo[Category]
  lazy val products: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Subcategory.this.type, Product]    = hasMany[Product]
  lazy val sales: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Subcategory.this.type, Sale]          = hasMany[Sale]
}

object Subcategory extends ActiveRecordCompanion[Subcategory] with PlayFormSupport[Subcategory] {
  implicit val format: OFormat[Subcategory] = Json.format[Subcategory]
}
