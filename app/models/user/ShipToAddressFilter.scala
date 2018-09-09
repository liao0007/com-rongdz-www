package models.user

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class ShipToAddressFilter(idOpt: Option[Long] = None,
                               userIdOpt: Option[Long] = None,
                               mobileOpt: Option[String] = None,
                               nameOpt: Option[String] = None,
                               addressOpt: Option[String] = None,
                               districtIdOpt: Option[Long] = None,
                               isDefaultOpt: Option[Boolean] = None)
  extends ModelFilter[ShipToAddress]

object ShipToAddressFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[ShipToAddressFilter] =
    new QueryStringBindable[ShipToAddressFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ShipToAddressFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val userIdOpt = longBinder.bind(key + ".uid", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val mobileOpt = stringBinder.bind(key + ".mobile", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val addressOpt = stringBinder.bind(key + ".address", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val districtIdOpt = longBinder.bind(key + ".district", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val isDefaultOpt = booleanBinder.bind(key + ".default", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        Some(
          Right(
            ShipToAddressFilter(
              idOpt,
              userIdOpt,
              mobileOpt,
              nameOpt,
              addressOpt,
              districtIdOpt,
              isDefaultOpt
            )))
      }

      override def unbind(key: String, filter: ShipToAddressFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.userIdOpt map (value => longBinder.unbind(key + ".uid", value)),
          filter.mobileOpt map (value => stringBinder.unbind(key + ".mobile", value)),
          filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
          filter.addressOpt map (value => stringBinder.unbind(key + ".address", value)),
          filter.districtIdOpt map (value => longBinder.unbind(key + ".district", value)),
          filter.isDefaultOpt map (value => booleanBinder.unbind(key + ".default", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[ShipToAddress]] = Json.format[ShipToAddressFilter].asInstanceOf[OFormat[ModelFilter[ShipToAddress]]]
}
