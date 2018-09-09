package entities.user

import entities.Response
import entities.auth.Token

case class SmsTokenResponse(token: Token) extends Response
