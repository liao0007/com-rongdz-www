package models.user

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 4/13/16.
  */
case class LoginInfo(
                          var userId: Long,
                          var providerId: String,
                          var providerKey: String
                        ) extends ActiveRecord {
  lazy val user: ActiveRecord.BelongsToAssociation[LoginInfo.this.type, User] = belongsTo[User]
  lazy val passwordInfo: ActiveRecord.HasOneAssociation[LoginInfo.this.type, PasswordInfo] =
    hasOne[PasswordInfo]
}

object LoginInfo extends ActiveRecordCompanion[LoginInfo] with PlayFormSupport[LoginInfo] {

  sealed class ProviderId(val name: String) extends EnumAttributeValue

  object LoginInfoProviderId extends EnumAttribute[ProviderId] {

    case object Credentials extends ProviderId(CredentialsProvider.ID.capitalize)

    case object Wechat extends ProviderId("Wechat")

    protected def all: Seq[ProviderId] = Seq[ProviderId](Credentials, Wechat)

  }

  implicit val format: OFormat[LoginInfo] = Json.format[LoginInfo]
}
