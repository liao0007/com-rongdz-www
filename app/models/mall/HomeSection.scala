package models.mall

import java.util.Date

import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Json, OFormat}

case class HomeSection(
                        override val id: Long = 0L,
                        var presentingType: String,
                        var title: String,
                        var headerBackgroundColor: String = "#FFF",
                        var headerBackgroundImage: Option[String] = None,
                        var sideImageLinks: Option[String] = None,
                        var sequence: Int,
                        var saleIds: String,
                        var headerLinks: Option[String] = None,
                        var footerImageLinks: Option[String] = None,
                        var startAt: Date,
                        var closeAt: Date
                      ) extends ActiveRecord {
  lazy val sales: ActiveRecord.Relation1[Sale, Sale] = Sale.where(_.id in saleIds.split(","))
}

object HomeSection extends ActiveRecordCompanion[HomeSection] with PlayFormSupport[HomeSection] {

  sealed abstract class PresentingTypeValue(val name: String) extends EnumAttributeValue

  object PresentingType extends EnumAttribute[PresentingTypeValue] {

    case object ScreenWidth extends PresentingTypeValue("ScreenWidth")

    case object HalfScreenWidth extends PresentingTypeValue("HalfScreenWidth")

    override protected def all: Seq[PresentingTypeValue] = Seq[PresentingTypeValue](ScreenWidth, HalfScreenWidth)
  }

  implicit val jsonFormat: OFormat[HomeSection] = Json.format[HomeSection]
}
