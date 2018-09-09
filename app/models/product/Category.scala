package models.product

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.mall.Sale
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
  lazy val subcategories: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Category.this.type, Subcategory] =
    hasMany[Subcategory]
  lazy val products: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Category.this.type, Product] = hasMany[Product]
  lazy val sales: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[Category.this.type, Sale]       = hasMany[Sale]
}

object Category extends ActiveRecordCompanion[Category] with PlayFormSupport[Category] {

  sealed class HoverType(val name: String)
  object CategoryHoverType extends IterableAttribute[HoverType] {
    case object None   extends HoverType("无")
    case object Fabric extends HoverType("面料")
    case object Normal extends HoverType("普通")
    protected def all: Seq[HoverType]                     = Seq[HoverType](None, Fabric, Normal)
    implicit def fromString(x: String): Option[HoverType] = all.find(_.toString == x)
    implicit def toString(state: HoverType): String       = state.toString
  }

  implicit val format: OFormat[Category] = Json.format[Category]
}
