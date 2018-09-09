package models.user

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.IterableAttribute
import models.{ActiveRecord, IterableAttribute}
import play.api.libs.json._

/**
  * Created by liangliao on 4/13/16.
  */
case class ToPermission(
    var userId: Long,
    var permission: String
) extends ActiveRecord {
  lazy val user: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[ToPermission.this.type, User] = belongsTo[User]
}

object ToPermission extends ActiveRecordCompanion[ToPermission] with PlayFormSupport[ToPermission] {

  sealed class Permission(val name: String)
  object Permission {
    implicit val format: Format[Permission] = new Format[Permission] {
      override def reads(json: JsValue): JsResult[Permission] = json match {
        case JsString(x) =>
          UserToPermission.fromString(x).map(JsSuccess(_)).getOrElse[JsResult[Permission]](JsError(s"No state found for $json"))
        case _ => JsError(s"Can't parse $json to user permission")
      }

      override def writes(o: Permission): JsValue = JsString(o.toString)
    }
  }

  object UserToPermission extends IterableAttribute[Permission] {
    case object Admin          extends Permission("管理员")
    val all = Seq(Admin)

    implicit def fromString(x: String): Option[Permission] = all.find(_.toString == x)
    implicit def toString(name: Permission): String        = name.toString
  }

  implicit val format: OFormat[ToPermission] = Json.format[ToPermission]

}
