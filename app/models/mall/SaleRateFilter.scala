package models.mall

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class SaleRateFilter(idOpt: Option[Long] = None,
                          codeOpt: Option[String] = None,
                          descriptionOpt: Option[String] = None,
                          imageOpt: Option[String] = None,
                          rateTypeOpt: Option[String] = None,
                          rateOpt: Option[String] = None,
                          startAtOpt: Option[String] = None,
                          closeAtOpt: Option[String] = None)
  extends ModelFilter[SaleRate]

object SaleRateFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[SaleRateFilter] =
    new QueryStringBindable[SaleRateFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SaleRateFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val codeOpt = stringBinder.bind(key + ".code", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val descriptionOpt = stringBinder.bind(key + ".desc", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val imageOpt = stringBinder.bind(key + ".image", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val rateTypeOpt = stringBinder.bind(key + ".rateType", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val rateOpt = stringBinder.bind(key + ".rate", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val startAtOpt = stringBinder.bind(key + ".startAt", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val closeAtOpt = stringBinder.bind(key + ".closeAt", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(Right(SaleRateFilter(idOpt, codeOpt, descriptionOpt, imageOpt, rateTypeOpt, rateOpt, startAtOpt, closeAtOpt)))
      }

      override def unbind(key: String, filter: SaleRateFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.codeOpt map (value => stringBinder.unbind(key + ".code", value)),
          filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value)),
          filter.imageOpt map (value => stringBinder.unbind(key + ".image", value)),
          filter.rateTypeOpt map (value => stringBinder.unbind(key + ".rateType", value)),
          filter.rateOpt map (value => stringBinder.unbind(key + ".rate", value)),
          filter.startAtOpt map (value => stringBinder.unbind(key + ".startAt", value)),
          filter.closeAtOpt map (value => stringBinder.unbind(key + ".closeAt", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[SaleRate]] = Json.format[SaleRateFilter].asInstanceOf[OFormat[ModelFilter[SaleRate]]]
}
