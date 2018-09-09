package models.product

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class SkuFilter(idOpt: Option[Long] = None,
                     productIdOpt: Option[Long] = None,
                     skuOpt: Option[String] = None,
                     unitPriceOpt: Option[String] = None
                    ) extends ModelFilter[Sku]

object SkuFilter {
  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[SkuFilter] =
    new QueryStringBindable[SkuFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SkuFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val productIdOpt = longBinder.bind(key + ".pid", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val skuOpt = stringBinder.bind(key + ".sku", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val unitPriceOpt = stringBinder.bind(key + ".price", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }


        Some(Right(SkuFilter(idOpt, productIdOpt, skuOpt, unitPriceOpt)))
      }

      override def unbind(key: String, filter: SkuFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.productIdOpt map (value => longBinder.unbind(key + ".pid", value)),
          filter.skuOpt map (value => stringBinder.unbind(key + ".sku", value)),
          filter.unitPriceOpt map (value => stringBinder.unbind(key + ".price", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Sku]] = Json.format[SkuFilter].asInstanceOf[OFormat[ModelFilter[Sku]]]
}
