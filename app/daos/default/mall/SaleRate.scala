package daos.default.mall

import java.util.Date

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.{ActiveRecord, IterableAttribute}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.{Json, OFormat}

case class SaleRate(
    override val id: Long = 0L,
    var code: String,
    var description: String,
    var image: String,
    var rateType: String,
    var rate: Long,
    var startAt: Date,
    var closeAt: Date
) extends ActiveRecord

object SaleRate extends ActiveRecordCompanion[SaleRate] with PlayFormSupport[SaleRate] {

  def newSaleRateNumber(sku: String): String = {
    val pattern = DateTimeFormat.forPattern("yyyyMMddHHmmss")
    sku + pattern.print(DateTime.now())
  }

  abstract class RateType(val name: String)
  object SaleRateType extends IterableAttribute[RateType] {
    case object SpecialOffer extends RateType("特价")
    case object Corporate    extends RateType("企业")
    case object Group        extends RateType("团体")
    implicit def fromString(x: String): Option[RateType] = all.find(_.toString == x)
    implicit def toString(state: RateType): String       = state.toString

    override protected def all: Seq[RateType] = Seq[RateType](SpecialOffer, Corporate, Group)
  }

  implicit val jsonFormat: OFormat[SaleRate] = Json.format[SaleRate]
}
