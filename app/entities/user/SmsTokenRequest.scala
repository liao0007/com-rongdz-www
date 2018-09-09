package entities.user

import play.api.libs.json.{Json, OFormat}

case class SmsTokenRequest(identifier: String)

object SmsTokenRequest {
  implicit val format: OFormat[SmsTokenRequest] = Json.format[SmsTokenRequest]
}
