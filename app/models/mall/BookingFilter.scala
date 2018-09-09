package models.mall

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class BookingFilter(idOpt: Option[Long] = None,
                         bookingNumberOpt: Option[String] = None,
                         nameOpt: Option[String] = None,
                         mobileOpt: Option[String] = None,
                         cityOpt: Option[String] = None,
                         addressOpt: Option[String] = None,
                         memoOpt: Option[String] = None,
                         stateOpt: Option[String] = None)
  extends ModelFilter[Booking]

object BookingFilter {
  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[BookingFilter] =
    new QueryStringBindable[BookingFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BookingFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }
        val bookingNumberOpt = stringBinder.bind(key + ".bkn", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val mobileOpt = stringBinder.bind(key + ".mob", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val cityOpt = stringBinder.bind(key + ".city", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val addressOpt = stringBinder.bind(key + ".add", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val memoOpt = stringBinder.bind(key + ".memo", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val stateOpt = stringBinder.bind(key + ".stat", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(Right(BookingFilter(idOpt, bookingNumberOpt, nameOpt, mobileOpt, cityOpt, addressOpt, memoOpt, stateOpt)))
      }

      override def unbind(key: String, filter: BookingFilter): String = {
        Seq(
          filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.bookingNumberOpt map (value => stringBinder.unbind(key + ".bkn", value)),
          filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
          filter.mobileOpt map (value => stringBinder.unbind(key + ".mob", value)),
          filter.cityOpt map (value => stringBinder.unbind(key + ".city", value)),
          filter.addressOpt map (value => stringBinder.unbind(key + ".add", value)),
          filter.memoOpt map (value => stringBinder.unbind(key + ".memo", value)),
          filter.stateOpt map (value => stringBinder.unbind(key + ".stat", value))
        ).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Booking]] = Json.format[BookingFilter].asInstanceOf[OFormat[ModelFilter[Booking]]]
}
