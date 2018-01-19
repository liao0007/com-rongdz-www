package models.default.product

import daos.default.product.Brand
import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class BrandFilter(idOpt: Option[Long] = None, nameOpt: Option[String] = None) extends ModelFilter[Brand]

object BrandFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[BrandFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BrandFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(BrandFilter(idOpt, nameOpt)))
      }

      override def unbind(key: String, filter: BrandFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.nameOpt map (value => stringBinder.unbind(key + ".name", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Brand]] = Json.format[BrandFilter].asInstanceOf[OFormat[ModelFilter[Brand]]]
}
