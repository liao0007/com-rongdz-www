package models.user

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 4/13/16.
  */
case class PasswordInfo(
                             var loginInfoId: Long,
                             var hasher: String,
                             var password: String,
                             var salt: Option[String] = None
                           ) extends ActiveRecord {
  lazy val loginInfo: ActiveRecord.BelongsToAssociation[PasswordInfo.this.type, LoginInfo] = belongsTo[LoginInfo]
}

object PasswordInfo extends ActiveRecordCompanion[PasswordInfo] with PlayFormSupport[PasswordInfo] {
  implicit val format: OFormat[PasswordInfo] = Json.format[PasswordInfo]
}
