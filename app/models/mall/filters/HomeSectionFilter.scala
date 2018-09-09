package models.mall.filters

import models.ModelFilter
import models.mall.HomeSection
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class HomeSectionFilter(idOpt: Option[Long] = None,
                             presentingTypeOpt: Option[String] = None,
                             titleOpt: Option[String] = None,
                             sequenceOpt: Option[String] = None,
                             saleIdsOpt: Option[String] = None,
                             startAtOpt: Option[String] = None,
                             closeAtOpt: Option[String] = None
                            ) extends ModelFilter[HomeSection]

object HomeSectionFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[HomeSectionFilter] =
    new QueryStringBindable[HomeSectionFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, HomeSectionFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val presentingTypeOpt = stringBinder.bind(key + ".prest", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val titleOpt = stringBinder.bind(key + ".titl", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val sequenceOpt = stringBinder.bind(key + ".seq", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val saleIdsOpt = stringBinder.bind(key + ".saleids", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val startAtOpt = stringBinder.bind(key + ".start", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }
        val closeAtOpt = stringBinder.bind(key + ".close", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(Right(HomeSectionFilter(idOpt, presentingTypeOpt, titleOpt, sequenceOpt, saleIdsOpt, startAtOpt, closeAtOpt)))
      }

      override def unbind(key: String, filter: HomeSectionFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.presentingTypeOpt map (value => stringBinder.unbind(key + ".prest", value)),
          filter.titleOpt map (value => stringBinder.unbind(key + ".titl", value)),
          filter.sequenceOpt map (value => stringBinder.unbind(key + ".seq", value)),
          filter.saleIdsOpt map (value => stringBinder.unbind(key + ".saleids", value)),
          filter.startAtOpt map (value => stringBinder.unbind(key + ".start", value)),
          filter.closeAtOpt map (value => stringBinder.unbind(key + ".close", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[HomeSection]] = Json.format[HomeSectionFilter].asInstanceOf[OFormat[ModelFilter[HomeSection]]]
}
