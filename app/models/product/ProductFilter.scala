package models.product

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class ProductFilter(
    idOpt: Option[Long] = None,
    brandIdOpt: Option[Long] = None,
    subcategoryIdOpt: Option[Long] = None,
    mkuOpt: Option[String] = None,
    nameOpt: Option[String] = None
) extends ModelFilter[Product]

object ProductFilter {
  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[ProductFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ProductFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val brandIdOpt = longBinder.bind(key + ".bid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val subcategoryIdOpt = longBinder.bind(key + ".scid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val mkuOpt = stringBinder.bind(key + ".mku", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val nameOpt = stringBinder.bind(key + ".titl", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(ProductFilter(idOpt, brandIdOpt, subcategoryIdOpt, mkuOpt, nameOpt)))
      }

      override def unbind(key: String, filter: ProductFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.brandIdOpt map (value => longBinder.unbind(key + ".bid", value)),
            filter.subcategoryIdOpt map (value => longBinder.unbind(key + ".scid", value)),
            filter.mkuOpt map (value => stringBinder.unbind(key + ".mku", value)),
            filter.nameOpt map (value => stringBinder.unbind(key + ".name", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Product]] = Json.format[ProductFilter].asInstanceOf[OFormat[ModelFilter[Product]]]

}
