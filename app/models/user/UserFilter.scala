package models.user

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class UserFilter(idOpt: Option[Long] = None,
                      mobileOpt: Option[String] = None,
                      nameOpt: Option[String] = None,
                      genderOpt: Option[String] = None,
                      birthdayOpt: Option[String] = None,
                      emailOpt: Option[String] = None,
                      stateOpt: Option[String] = None)
    extends ModelFilter[User]

object UserFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[UserFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, UserFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val mobileOpt = stringBinder.bind(key + ".mobile", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val genderOpt = stringBinder.bind(key + ".gender", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val birthdayOpt = stringBinder.bind(key + ".birthday", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val emailOpt = stringBinder.bind(key + ".email", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val stateOpt = stringBinder.bind(key + ".state", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(UserFilter(idOpt, mobileOpt, nameOpt, genderOpt, birthdayOpt, emailOpt, stateOpt)))
      }

      override def unbind(key: String, filter: UserFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.mobileOpt map (value => stringBinder.unbind(key + ".mobile", value)),
            filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
            filter.genderOpt map (value => stringBinder.unbind(key + ".gender", value)),
            filter.birthdayOpt map (value => stringBinder.unbind(key + ".birthday", value)),
            filter.emailOpt map (value => stringBinder.unbind(key + ".email", value)),
            filter.stateOpt map (value => stringBinder.unbind(key + ".state", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[User]] = Json.format[UserFilter].asInstanceOf[OFormat[ModelFilter[User]]]
}
