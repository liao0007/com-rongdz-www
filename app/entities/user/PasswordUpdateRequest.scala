package entities.user

import play.api.libs.json.{Json, OFormat}

case class PasswordUpdateRequest(identifier: String, password: String)

object PasswordUpdateRequest {
  implicit val format: OFormat[PasswordUpdateRequest] = Json.format[PasswordUpdateRequest]
}
