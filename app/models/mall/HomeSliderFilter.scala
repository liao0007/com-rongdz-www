package models.mall

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class HomeSliderFilter(idOpt: Option[Long] = None,
                            contentOpt: Option[String] = None,
                            sequenceOpt: Option[String] = None,
                            startAtOpt: Option[String] = None,
                            closeAtOpt: Option[String] = None)
    extends ModelFilter[HomeSlider]

object HomeSliderFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[HomeSliderFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, HomeSliderFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val contentOpt = stringBinder.bind(key + ".cont", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val sequenceOpt = stringBinder.bind(key + ".seq", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val startAtOpt = stringBinder.bind(key + ".start", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val closeAtOpt = stringBinder.bind(key + ".close", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(HomeSliderFilter(idOpt, contentOpt, sequenceOpt, startAtOpt, closeAtOpt)))
      }

      override def unbind(key: String, filter: HomeSliderFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.contentOpt map (value => stringBinder.unbind(key + ".cont", value)),
            filter.sequenceOpt map (value => stringBinder.unbind(key + ".seq", value)),
            filter.startAtOpt map (value => stringBinder.unbind(key + ".start", value)),
            filter.closeAtOpt map (value => stringBinder.unbind(key + ".close", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[HomeSlider]] = Json.format[HomeSliderFilter].asInstanceOf[OFormat[ModelFilter[HomeSlider]]]
}
