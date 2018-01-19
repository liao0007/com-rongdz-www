package models.default.mall

import daos.default.mall.SaleOrderDetailAttributeValue
import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class SaleOrderDetailAttributeValueFilter(idOpt: Option[Long] = None,
                                               saleOrderDetailIdOpt: Option[Long] = None,
                                               attributeIdOpt: Option[Long] = None,
                                               valueOpt: Option[String] = None)
    extends ModelFilter[SaleOrderDetailAttributeValue]

object SaleOrderDetailAttributeValueFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[SaleOrderDetailAttributeValueFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SaleOrderDetailAttributeValueFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val saleOrderDetailIdOpt = longBinder.bind(key + ".sodid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val attributeIdOpt = longBinder.bind(key + ".attid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val valueOpt = stringBinder.bind(key + ".value", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(SaleOrderDetailAttributeValueFilter(idOpt, saleOrderDetailIdOpt, attributeIdOpt, valueOpt)))
      }

      override def unbind(key: String, filter: SaleOrderDetailAttributeValueFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.saleOrderDetailIdOpt map (value => longBinder.unbind(key + ".sodid", value)),
            filter.attributeIdOpt map (value => longBinder.unbind(key + ".attid", value)),
            filter.valueOpt map (value => stringBinder.unbind(key + ".value", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[SaleOrderDetailAttributeValue]] =
    Json.format[SaleOrderDetailAttributeValueFilter].asInstanceOf[OFormat[ModelFilter[SaleOrderDetailAttributeValue]]]
}
