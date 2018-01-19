package daos.default.mall

import java.util.Date

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}
import com.github.aselab.activerecord.dsl._

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
  lazy val sales: _root_.com.github.aselab.activerecord.ActiveRecord.Relation1[Sale, Sale] = Sale.where(_.id in saleIds.split(","))
}

object HomeSection extends ActiveRecordCompanion[HomeSection] with PlayFormSupport[HomeSection] {

  sealed trait PresentingType
  object HomeSectionPresentingType {
    case object ScreenWidth     extends PresentingType
    case object HalfScreenWidth extends PresentingType
    implicit def fromString(x: String): Option[PresentingType] =
      Seq[PresentingType](ScreenWidth, HalfScreenWidth).find(_.toString == x)
    implicit def toString(state: PresentingType): String = state.toString
  }

  implicit val jsonFormat: OFormat[HomeSection] = Json.format[HomeSection]
}
