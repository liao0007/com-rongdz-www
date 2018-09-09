package entities.auth

import com.mohiva.play.silhouette.api.util.Credentials
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AuthRequest(identifier: String, password: String) {
  lazy val toCredentials: Credentials = Credentials(identifier, password)
}

object AuthRequest {
  implicit val format: Format[AuthRequest] = {
    val reads: Reads[AuthRequest] =
      ((__ \ "identifier").read[String].filter(JsonValidationError("请填写有效手机号码"))(_.matches("""^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$""")) and (__ \ "password").read[String]) (AuthRequest.apply _)
    val write: Writes[AuthRequest] =
      ((__ \ "identifier").write[String] and (__ \ "password").write[String]) (unlift(AuthRequest.unapply))
    Format(reads, write)
  }
}
