package models.mall

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class SaleOrderFilter(idOpt: Option[Long] = None,
                           orderNumberOpt: Option[String] = None,
                           shipToNameOpt: Option[String] = None,
                           shipToMobileOpt: Option[String] = None,
                           totalAmountOpt: Option[String] = None,
                           channelOpt: Option[String] = None,
                           deliveryTypeOpt: Option[String] = None,
                           stateOpt: Option[String] = None,
                           paymentStateOpt: Option[String] = None,
                           shippingStateOpt: Option[String] = None,
                           memoOpt: Option[String] = None)
  extends ModelFilter[SaleOrder]

object SaleOrderFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[SaleOrderFilter] =
    new QueryStringBindable[SaleOrderFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SaleOrderFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val orderNumberOpt = stringBinder.bind(key + ".num", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val shipToNameOpt = stringBinder.bind(key + ".shname", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val shipToMobileOpt = stringBinder.bind(key + ".shmob", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val totalAmountOpt = stringBinder.bind(key + ".total", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val channelOpt = stringBinder.bind(key + ".ch", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val deliveryTypeOpt = stringBinder.bind(key + ".deltype", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val stateOpt = stringBinder.bind(key + ".stat", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val paymentStateOpt = stringBinder.bind(key + ".paystat", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val shippingStateOpt = stringBinder.bind(key + ".shstat", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val memoOpt = stringBinder.bind(key + ".memo", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(
          Right(
            SaleOrderFilter(idOpt,
              orderNumberOpt,
              shipToNameOpt,
              shipToMobileOpt,
              totalAmountOpt,
              channelOpt,
              deliveryTypeOpt,
              stateOpt,
              paymentStateOpt,
              shippingStateOpt,
              memoOpt)))
      }

      override def unbind(key: String, filter: SaleOrderFilter): String = {
        Seq(
          filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.orderNumberOpt map (value => stringBinder.unbind(key + ".num", value)),
          filter.shipToNameOpt map (value => stringBinder.unbind(key + ".shname", value)),
          filter.shipToMobileOpt map (value => stringBinder.unbind(key + ".shmob", value)),
          filter.totalAmountOpt map (value => stringBinder.unbind(key + ".total", value)),
          filter.channelOpt map (value => stringBinder.unbind(key + ".ch", value)),
          filter.deliveryTypeOpt map (value => stringBinder.unbind(key + ".deltype", value)),
          filter.stateOpt map (value => stringBinder.unbind(key + ".stat", value)),
          filter.paymentStateOpt map (value => stringBinder.unbind(key + ".paystat", value)),
          filter.shippingStateOpt map (value => stringBinder.unbind(key + ".shstat", value)),
          filter.memoOpt map (value => stringBinder.unbind(key + ".memo", value))
        ).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[SaleOrder]] = Json.format[SaleOrderFilter].asInstanceOf[OFormat[ModelFilter[SaleOrder]]]
}
