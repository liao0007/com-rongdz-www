package models.default.crm

import daos.default.crm.Page
import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class PageFilter(idOpt: Option[Long] = None, nameOpt: Option[String] = None, contentOpt: Option[String] = None) extends ModelFilter[Page]

object PageFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[PageFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PageFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }
        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val contentOpt = stringBinder.bind(key + ".content", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(PageFilter(idOpt, nameOpt, contentOpt)))
      }

      override def unbind(key: String, filter: PageFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
            filter.contentOpt map (value => stringBinder.unbind(key + ".content", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Page]] = Json.format[PageFilter].asInstanceOf[OFormat[ModelFilter[Page]]]
}
