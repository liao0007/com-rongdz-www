package models.user

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by liangliao on 4/13/16.
  */
case class UserToken(
                      var userId: Long,
                      var token: String,
                      var expiresOn: DateTime,
                      var action: String
                    ) extends ActiveRecord {
  lazy val user: ActiveRecord.BelongsToAssociation[UserToken.this.type, User] = belongsTo[User]
}

object UserToken extends ActiveRecordCompanion[UserToken] with PlayFormSupport[UserToken] {

  sealed class Action(val name: String) extends EnumAttributeValue

  object ActionTokenAction extends EnumAttribute[Action] {

    case object ActivateAccount extends Action("激活账户")

    case object ResetPassword extends Action("重置密码")

    override protected def all: Seq[Action] = Seq(ActivateAccount, ResetPassword)
  }

  implicit val dateTimeJsWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("dd/MM/yyyy HH:mm:ss")
  implicit val format: OFormat[UserToken] = Json.format[UserToken]
}
