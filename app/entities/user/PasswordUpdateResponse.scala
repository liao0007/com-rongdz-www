package entities.user

import com.mohiva.play.silhouette.api.util.PasswordInfo
import entities.Response

case class PasswordUpdateResponse(override val code: Int = 0, override val message: String = "", passwordInfo: PasswordInfo) extends Response
