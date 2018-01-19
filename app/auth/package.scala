import java.time.ZoneId

import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordInfo}
import daos.default.user.ToPermission.Permission
import org.joda.time.DateTime
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by liangliao on 24/11/16.
  */
package object auth {

  //internal models
  case class UserCreated(userId: Long, token: Option[String] = None)
  case class SignedIn(userId: Long, token: AuthToken)
  case class PasswordUpdated(passwordInfo: PasswordInfo)
  case class SmsTokenGenerated(token: AuthToken)

  sealed trait UserStateError
  case object UserNotExists                      extends UserStateError
  case object UserAlreadyExists                  extends UserStateError
  case object UserNotActivated                   extends UserStateError
  case object UserDisabled                       extends UserStateError
  case object LoginInfoMissing                   extends UserStateError
  case object InvalidCredentials                 extends UserStateError
  case class ProviderError(e: ProviderException) extends UserStateError

  //request models
  case class SignUp(identifier: String, name: String, avatar: Option[String])
  object SignUp {
    implicit val signUpReads: Reads[SignUp] = (
      (JsPath \ "identifier").read[String].filter(ValidationError("请填写有效手机号码"))( _.matches("""^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$""")) and
        (JsPath \ "name").read[String] and (JsPath \ "avatar").readNullable[String]
      )(SignUp.apply _)
  }

  case class SignIn(identifier: String, password: String, rememberMe: Boolean = true) {
    lazy val toCredentials: Credentials = Credentials(identifier, password)
  }
  object SignIn {
    implicit val format: Format[SignIn] = {
      val reads: Reads[SignIn] =
        ((__ \ "identifier").read[String].filter(ValidationError("请填写有效手机号码"))( _.matches("""^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$""")) and (__ \ "password")
          .read[String] and (JsPath \ "rememberMe").read[String].map(_.toBoolean))(SignIn.apply _)
      val write: Writes[SignIn] =
        ((JsPath \ "identifier").write[String] and (__ \ "password").write[String] and (JsPath \ "rememberMe")
          .write[Boolean])(unlift(SignIn.unapply))
      Format(reads, write)
    }
  }

  case class AssignPermission(permission: Permission)
  object AssignPermission {
    implicit val format: OFormat[AssignPermission] =
      Json.format[AssignPermission]
  }

  // requests
  case class UpdateUserRequest(name: String, email: String)
  object UpdateUserRequest {
    implicit val format: OFormat[UpdateUserRequest] = Json.format[UpdateUserRequest]
  }

  case class PasswordUpdateRequest(identifier: String, password: String)
  object PasswordUpdateRequest {
    implicit val format: OFormat[PasswordUpdateRequest] = Json.format[PasswordUpdateRequest]
  }

  case class SmsTokenRequest(identifier: String)
  object SmsTokenRequest {
    implicit val format: OFormat[SmsTokenRequest] = Json.format[SmsTokenRequest]
  }

  //response models
  case class AuthToken(token: String, expiresOn: DateTime)
  object AuthToken {
    implicit val format: OFormat[AuthToken] = Json.format[AuthToken]
  }

  //conversions
  object Conversions {
    def javaToJoda(time: java.time.LocalDateTime): org.joda.time.LocalDateTime = {
      val zonedDateTime = time.atZone(ZoneId.systemDefault())
      val instant       = zonedDateTime.toInstant
      val millis        = instant.toEpochMilli
      new org.joda.time.LocalDateTime(millis)
    }

    def jodaToJava(time: org.joda.time.DateTime): java.time.LocalDateTime =
      java.time.LocalDateTime.of(
        time.getYear,
        time.getMonthOfYear,
        time.getDayOfMonth,
        time.getHourOfDay,
        time.getMinuteOfHour,
        time.getSecondOfMinute,
        time.getMillisOfSecond * 1000000
      ) // takes nano
  }
}
