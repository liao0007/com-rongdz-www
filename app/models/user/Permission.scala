package models.user

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json._

/**
  * Created by liangliao on 4/13/16.
  */
case class Permission(
                       var userId: Long,
                       var permission: String
                     ) extends ActiveRecord {
  lazy val user: ActiveRecord.BelongsToAssociation[Permission.this.type, User] = belongsTo[User]
}

object Permission extends ActiveRecordCompanion[Permission] with PlayFormSupport[Permission] {

  sealed class Permission(val name: String) extends EnumAttributeValue

  object UserPermissionPermission extends EnumAttribute[Permission] {

    case object Admin extends Permission("Admin")

    val all = Seq(Admin)
  }

  object Permission {
    implicit val format: Format[Permission] = new Format[Permission] {
      override def reads(json: JsValue): JsResult[Permission] = json match {
        case JsString(x) => UserPermissionPermission.fromString(x).map(JsSuccess(_)).getOrElse[JsResult[Permission]](JsError(s"No state found for $json"))
        case _ => JsError(s"Can't parse $json to user permission")
      }

      override def writes(o: Permission): JsValue = JsString(o.toString)
    }
  }

  implicit val format: OFormat[Permission] = Json.format[Permission]
}