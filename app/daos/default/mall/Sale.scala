package daos.default.mall

import java.util.Date

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import daos.default.product._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, OFormat}

case class Sale(
    override val id: Long = 0L,
    var saleNumber: String,
    var productId: Long,
    var skuId: Long,
    var brandId: Long,
    var categoryId: Long,
    var subcategoryId: Long,
    var title: String,
    var description: String,
    var unitPrice: Float,
    var originalUnitPrice: Float,
    var keyword: Option[String],
    var startAt: Date,
    var closeAt: Date,
    var sequence: Option[Int]
) extends ActiveRecord {
  lazy val product: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Sale.this.type, Product]   = belongsTo[Product]
  lazy val sku: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Sale.this.type, Sku]           = belongsTo[Sku]
  lazy val brand: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Sale.this.type, Brand]       = belongsTo[Brand]
  lazy val category: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Sale.this.type, Category] = belongsTo[Category]
  lazy val subcategory: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Sale.this.type, Subcategory] =
    belongsTo[Subcategory]
}

object Sale extends ActiveRecordCompanion[Sale] with PlayFormSupport[Sale] {

  def newSaleNumber(sku: String): String = {
    val pattern = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    sku + pattern.print(DateTime.now())
  }

  implicit val jsonFormat: OFormat[Sale] = Json.format[Sale]
}
