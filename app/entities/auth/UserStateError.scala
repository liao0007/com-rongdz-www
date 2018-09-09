package entities.auth

import com.mohiva.play.silhouette.api.exceptions.ProviderException

sealed trait UserStateError

case object UserNotExists extends UserStateError

case object UserAlreadyExists extends UserStateError

case object UserNotActivated extends UserStateError

case object UserDisabled extends UserStateError

case object LoginInfoMissing extends UserStateError

case object InvalidCredentials extends UserStateError

case class ProviderError(e: ProviderException) extends UserStateError