package entities.user

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsonValidationError, Reads, __}

case class RegistrationRequest(identifier: String, name: String, avatar: Option[String])

object RegistrationRequest {
  implicit val signUpReads: Reads[RegistrationRequest] = (
    (__ \ "identifier").read[String].filter(JsonValidationError("请填写有效手机号码"))(_.matches("""^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$"""))
      and (__ \ "name").read[String]
      and (__ \ "avatar").readNullable[String]
    ) (RegistrationRequest.apply _)
}
