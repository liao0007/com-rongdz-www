package daos.default.mall

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class SaleOrderDetail(
    override val id: Long = 0L,
    var saleOrderId: Long,
    var saleId: Long,
    var quantity: Int
) extends ActiveRecord {
  lazy val saleOrder: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[SaleOrderDetail.this.type, SaleOrder] =
    belongsTo[SaleOrder]
  lazy val sale: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[SaleOrderDetail.this.type, Sale] = belongsTo[Sale]
  lazy val saleOrderDetailAttributeValues: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[SaleOrderDetail.this.type,
                                                                                                                 SaleOrderDetailAttributeValue] =
    hasMany[SaleOrderDetailAttributeValue]
}

object SaleOrderDetail extends ActiveRecordCompanion[SaleOrderDetail] with PlayFormSupport[SaleOrderDetail] {
  implicit val jsonFormat: OFormat[SaleOrderDetail] = Json.format[SaleOrderDetail]
}
