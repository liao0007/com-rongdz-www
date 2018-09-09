package models.mall

import java.util.Date

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class HomeSlider(
                       override val id: Long,
                       var content: String,
                       var sequence: Int,
                       var startAt: Date,
                       var closeAt: Date
                     ) extends ActiveRecord

object HomeSlider extends ActiveRecordCompanion[HomeSlider] with PlayFormSupport[HomeSlider] {
  implicit val jsonFormat: OFormat[HomeSlider] = Json.format[HomeSlider]
}
