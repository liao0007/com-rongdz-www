package models.mall

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class BookingFollowupFilter(idOpt: Option[Long] = None, bookingIdOpt: Option[Long] = None, descriptionOpt: Option[String] = None)
    extends ModelFilter[BookingFollowup]

object BookingFollowupFilter {
  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[BookingFollowupFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BookingFollowupFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }
        val bookingIdOpt = longBinder.bind(key + ".bid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }
        val descriptionOpt = stringBinder.bind(key + ".desc", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        Some(Right(BookingFollowupFilter(idOpt, bookingIdOpt, descriptionOpt)))
      }

      override def unbind(key: String, filter: BookingFollowupFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.bookingIdOpt map (value => longBinder.unbind(key + ".bid", value)),
            filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[BookingFollowup]] = Json.format[BookingFollowupFilter].asInstanceOf[OFormat[ModelFilter[BookingFollowup]]]
}
