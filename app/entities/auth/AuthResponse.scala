package entities.auth

import entities.Response
import org.joda.time.DateTime
import play.api.libs.json._

case class AuthResponse(override val code: Int = 0,
                        override val message: String = "",
                        token: Option[String] = None,
                        expiresOn: Option[DateTime] = None
                       ) extends Response

object AuthResponse {
  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("dd/MM/yyyy HH:mm:ss")

  implicit val format: OFormat[AuthResponse] = Json.format[AuthResponse]
}