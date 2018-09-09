package models

import auth.environments.JWTEnv
import com.github.aselab.activerecord.Timestamps
import com.github.aselab.activerecord.inner.{CRUDable, Optimistic}
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import models.user.User
import play.api.mvc.RequestHeader

/**
  * Created by liangliao on 17/7/16.
  */
abstract class ActiveRecord extends com.github.aselab.activerecord.ActiveRecord
  with Optimistic
  with Timestamps
  with Userstamps
  with Statuses

trait EnumAttributeValue {
  val name: String

  override def toString: String = name.toLowerCase
}

trait EnumAttribute[T <: EnumAttributeValue] extends Seq[T] {

  protected def all: Seq[T]

  override def length: Int = all.length

  override def apply(idx: Int): T = all(idx)

  override def iterator: Iterator[T] = all.toIterator

  implicit def fromString(string: String): Option[T] = all.find(_.toString == string)

  implicit def toString(value: T): String = value.toString
}

trait Statuses extends CRUDable {
  val active: Boolean = true
  val systemCreated: Boolean = false

  override def beforeDelete(): Unit = if (systemCreated) throw new Exception("unable to delete system created record")
}

trait Userstamps extends CRUDable {
  var createdBy: Option[String] = None
  var updatedBy: Option[String] = None

  def stampCreator(implicit request: RequestHeader): this.type = withIdentityOpt { identityOpt =>
    createdBy = identityOpt map (_.name)
  }

  def stampUpdater(implicit requestHeader: RequestHeader): this.type = withIdentityOpt { identityOpt =>
    updatedBy = identityOpt map (_.name)
  }

  private def withIdentityOpt(update: Option[User] => Unit)(implicit request: RequestHeader): this.type = {
    val identityOpt = request match {
      case r: UserAwareRequest[JWTEnv@unchecked, _] => r.identity
      case r: SecuredRequest[JWTEnv@unchecked, _] => Some(r.identity)
      case _ => None
    }
    update(identityOpt)
    this
  }

}