package models.mall

import java.util.Date

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
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

  abstract class RateType(val name: String) extends EnumAttributeValue

  object SaleRateType extends EnumAttribute[RateType] {

    case object SpecialOffer extends RateType("SpecialOffer")

    case object Corporate extends RateType("Corporate")

    case object Group extends RateType("Group")

    override protected def all: Seq[RateType] = Seq[RateType](SpecialOffer, Corporate, Group)
  }

  implicit val jsonFormat: OFormat[SaleRate] = Json.format[SaleRate]
}
