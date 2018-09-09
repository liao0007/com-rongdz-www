package models.user

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class LoginInfoFilter(idOpt: Option[Long] = None,
                           userIdOpt: Option[Long] = None,
                           providerIdOpt: Option[String] = None,
                           providerKeyOpt: Option[String] = None)
  extends ModelFilter[LoginInfo]

object LoginInfoFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[LoginInfoFilter] =
    new QueryStringBindable[LoginInfoFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LoginInfoFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val userIdOpt = longBinder.bind(key + ".uid", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val providerIdOpt = stringBinder.bind(key + ".pid", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val providerKeyOpt = stringBinder.bind(key + ".pkey", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(Right(LoginInfoFilter(idOpt, userIdOpt, providerIdOpt, providerKeyOpt)))
      }

      override def unbind(key: String, filter: LoginInfoFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.userIdOpt map (value => longBinder.unbind(key + ".uid", value)),
          filter.providerIdOpt map (value => stringBinder.unbind(key + ".pid", value)),
          filter.providerKeyOpt map (value => stringBinder.unbind(key + ".pkey", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[LoginInfo]] = Json.format[LoginInfoFilter].asInstanceOf[OFormat[ModelFilter[LoginInfo]]]
}
