package models.user

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import org.joda.time.DateTime
import play.api.libs.json._

/**
  * Created by liangliao on 4/13/16.
  */
case class Token(
                  var userId: Long,
                  var token: String,
                  var expiresOn: DateTime,
                  var action: String
                ) extends ActiveRecord {
  lazy val user: ActiveRecord.BelongsToAssociation[Token.this.type, User] = belongsTo[User]
}

object Token extends ActiveRecordCompanion[Token] with PlayFormSupport[Token] {

  sealed class Action(val name: String) extends EnumAttributeValue

  object Actions extends EnumAttribute[Action] {

    case object ActivateAccount extends Action("ActivateAccount")

    case object ResetPassword extends Action("ResetPassword")

    override protected def all: Seq[Action] = Seq(ActivateAccount, ResetPassword)
  }

  implicit val dateTimeJsWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("dd/MM/yyyy HH:mm:ss")
  implicit val format: OFormat[Token] = Json.format[Token]
}
