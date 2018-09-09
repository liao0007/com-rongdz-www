package models.product

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.mall.Sale
import models.product.Category.CategoryHoverType
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Json, OFormat}

case class Category(
                     override val id: Long = 0L,
                     var name: String,
                     var description: String,
                     var bannerImage: Option[String] = None,
                     var guideLink: Option[String] = None,
                     var isCustom: Boolean = true,
                     var hoverType: String = CategoryHoverType.None
                   ) extends ActiveRecord {
  lazy val subcategories: ActiveRecord.HasManyAssociation[Category.this.type, Subcategory] =
    hasMany[Subcategory]
  lazy val products: ActiveRecord.HasManyAssociation[Category.this.type, Product] = hasMany[Product]
  lazy val sales: ActiveRecord.HasManyAssociation[Category.this.type, Sale] = hasMany[Sale]
}

object Category extends ActiveRecordCompanion[Category] with PlayFormSupport[Category] {

  sealed class HoverType(val name: String) extends EnumAttributeValue

  object CategoryHoverType extends EnumAttribute[HoverType] {

    case object None extends HoverType("None")

    case object Fabric extends HoverType("Fabric")

    case object Normal extends HoverType("Normal")

    protected def all: Seq[HoverType] = Seq[HoverType](None, Fabric, Normal)
  }

  implicit val format: OFormat[Category] = Json.format[Category]
}
