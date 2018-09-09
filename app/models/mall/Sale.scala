package models.mall

import java.util.Date

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import models.product._
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
  lazy val product: ActiveRecord.BelongsToAssociation[Sale.this.type, Product] = belongsTo[Product]
  lazy val sku: ActiveRecord.BelongsToAssociation[Sale.this.type, Sku] = belongsTo[Sku]
  lazy val brand: ActiveRecord.BelongsToAssociation[Sale.this.type, Brand] = belongsTo[Brand]
  lazy val category: ActiveRecord.BelongsToAssociation[Sale.this.type, Category] = belongsTo[Category]
  lazy val subcategory: ActiveRecord.BelongsToAssociation[Sale.this.type, Subcategory] =
    belongsTo[Subcategory]
}

object Sale extends ActiveRecordCompanion[Sale] with PlayFormSupport[Sale] {

  def newSaleNumber(sku: String): String = {
    val pattern = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    sku + pattern.print(DateTime.now())
  }

  implicit val jsonFormat: OFormat[Sale] = Json.format[Sale]
}
