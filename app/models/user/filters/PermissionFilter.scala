package models.user.filters

import models.ModelFilter
import models.user.Permission
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class PermissionFilter(idOpt: Option[Long] = None, userIdOpt: Option[Long] = None, permissionOpt: Option[String] = None)
  extends ModelFilter[Permission]

object PermissionFilter {
  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[PermissionFilter] =
    new QueryStringBindable[PermissionFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PermissionFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val userIdOpt = longBinder.bind(key + ".uid", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val permissionOpt = stringBinder.bind(key + ".permission", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(Right(PermissionFilter(idOpt, userIdOpt, permissionOpt)))
      }

      override def unbind(key: String, filter: PermissionFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.userIdOpt map (value => longBinder.unbind(key + ".uid", value)),
          filter.permissionOpt map (value => stringBinder.unbind(key + ".permission", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Permission]] = Json.format[PermissionFilter].asInstanceOf[OFormat[ModelFilter[Permission]]]
}
