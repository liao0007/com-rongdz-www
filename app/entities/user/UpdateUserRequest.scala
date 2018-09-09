package entities.user

import play.api.libs.json.{Json, OFormat}

case class UpdateUserRequest(name: String, email: String)

object UpdateUserRequest {
  implicit val format: OFormat[UpdateUserRequest] = Json.format[UpdateUserRequest]
}