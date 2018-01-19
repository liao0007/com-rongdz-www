package auth.services

import play.api.libs.Codecs

class PasswordHashService {
  private[this] val md           = java.security.MessageDigest.getInstance("SHA-1")
  def hash(text: String): String = Codecs.sha1(md.digest(text.getBytes))
}
