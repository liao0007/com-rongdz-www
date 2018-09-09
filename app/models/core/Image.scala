package models.core

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class Image(
                  override val id: Long = 0L,
                  var url: String
                ) extends ActiveRecord

object Image extends ActiveRecordCompanion[Image] with PlayFormSupport[Image] {
  implicit val format: OFormat[Image] = Json.format[Image]
}