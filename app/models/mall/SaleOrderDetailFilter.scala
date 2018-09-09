package models.mall

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class SaleOrderDetailFilter(idOpt: Option[Long] = None,
                                 saleOrderIdOpt: Option[Long] = None,
                                 saleIdOpt: Option[Long] = None,
                                 quantity: Option[String] = None)
    extends ModelFilter[SaleOrderDetail]

object SaleOrderDetailFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[SaleOrderDetailFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SaleOrderDetailFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val saleOrderIdOpt = longBinder.bind(key + ".soid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val saleIdOpt = longBinder.bind(key + ".sid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val quantity = stringBinder.bind(key + ".qty", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(SaleOrderDetailFilter(idOpt, saleOrderIdOpt, saleIdOpt, quantity)))
      }

      override def unbind(key: String, filter: SaleOrderDetailFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.saleOrderIdOpt map (value => longBinder.unbind(key + ".soid", value)),
            filter.saleIdOpt map (value => longBinder.unbind(key + ".sid", value)),
            filter.quantity map (value => stringBinder.unbind(key + ".qty", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[SaleOrderDetail]] = Json.format[SaleOrderDetailFilter].asInstanceOf[OFormat[ModelFilter[SaleOrderDetail]]]
}
