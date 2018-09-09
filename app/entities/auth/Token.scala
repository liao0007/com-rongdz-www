package entities.auth

import org.joda.time.DateTime

case class Token(token: String, expiresOn: DateTime)