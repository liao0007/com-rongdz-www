package models.mall

import java.util.Date

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Json, OFormat}

case class HomeFeature(
                        override val id: Long = 0L,
                        var presentingType: String,
                        var smallTitle: String,
                        var title: String,
                        var backgroundImage: String,
                        var sequence: Int,
                        var link: String,
                        var startAt: Date,
                        var closeAt: Date
                      ) extends ActiveRecord

object HomeFeature extends ActiveRecordCompanion[HomeFeature] with PlayFormSupport[HomeFeature] {

  abstract class PresentingType(val name: String) extends EnumAttributeValue

  object HomeFeaturePresentingType extends EnumAttribute[PresentingType] {

    case object Custom extends PresentingType("定制")

    case object Collection extends PresentingType("成衣")

    case object Page extends PresentingType("主题")

    override protected def all: Seq[PresentingType] = Seq[PresentingType](Custom, Collection, Page)
  }

  implicit val jsonFormat: OFormat[HomeFeature] = Json.format[HomeFeature]
}
