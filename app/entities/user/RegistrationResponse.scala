package entities.user

import entities.Response
import org.joda.time.DateTime
import play.api.libs.json._

case class RegistrationResponse(override val code: Int = 0,
                                override val message: String = "",
                                userId: Option[Long] = None,
                                token: Option[String] = None,
                                expiresOn: Option[DateTime] = None) extends Response

object RegistrationResponse {
  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("dd/MM/yyyy HH:mm:ss")

  implicit val format: OFormat[RegistrationResponse] = Json.format[RegistrationResponse]
}