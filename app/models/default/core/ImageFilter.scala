package models.default.core

import daos.default.core.Image
import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class ImageFilter(idOpt: Option[Long] = None, urlOpt: Option[String] = None) extends ModelFilter[Image]

object ImageFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[ImageFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ImageFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val urlOpt = stringBinder.bind(key + ".url", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(ImageFilter(idOpt, urlOpt)))
      }

      override def unbind(key: String, filter: ImageFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.urlOpt map (value => stringBinder.unbind(key + ".url", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Image]] = Json.format[ImageFilter].asInstanceOf[OFormat[ModelFilter[Image]]]
}
