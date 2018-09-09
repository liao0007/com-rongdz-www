package auth.services

import play.api.libs.Codecs

class TokenService {
  private[this] val md = java.security.MessageDigest.getInstance("SHA-1")

  def hash(text: String, length: Int): String = Codecs.sha1(md.digest(text.getBytes)).take(length)
}
