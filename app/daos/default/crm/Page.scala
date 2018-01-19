package daos.default.crm

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import play.api.libs.json.{Json, OFormat}

case class Page(
    override val id: Long = 0L,
    var name: String,
    var content: String
) extends ActiveRecord

object Page extends ActiveRecordCompanion[Page] with PlayFormSupport[Page] {
  implicit val jsonFormat: OFormat[Page] = Json.format[Page]
}
