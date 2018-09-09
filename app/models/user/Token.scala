package models.user

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 4/13/16.
  */
case class Token(
    var userId: Long,
    var token: String,
    var expiresOn: DateTime,
    var action: String
) extends ActiveRecord {
  lazy val user: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[Token.this.type, User] = belongsTo[User]
}

object Token extends ActiveRecordCompanion[Token] with PlayFormSupport[Token] {
  sealed trait Action
  object TokenAction {
    case object ActivateAccount extends Action
    case object ResetPassword   extends Action
    implicit def fromString(x: String): Option[Action] =
      Seq[Action](ActivateAccount, ResetPassword).find(_.toString == x)
    implicit def toString(action: Action): String = action.toString
  }

  implicit val format: OFormat[Token] = Json.format[Token]
}
