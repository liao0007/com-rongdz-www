package entities.user

import auth.entities.Token
import entities.Response

case class SmsTokenResponse(token: Token) extends Response
