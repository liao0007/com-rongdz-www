package models.product

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class AttributeValueSetFilter(idOpt: Option[Long] = None,
                                   attributeSetIdOpt: Option[Long] = None,
                                   nameOpt: Option[String] = None,
                                   descriptionOpt: Option[String] = None)
    extends ModelFilter[AttributeValueSet]

object AttributeValueSetFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[AttributeValueSetFilter] =
    new QueryStringBindable[AttributeValueSetFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AttributeValueSetFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val attributeSetIdOpt = longBinder.bind(key + ".attsetid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val descriptionOpt = stringBinder.bind(key + ".desc", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        Some(Right(AttributeValueSetFilter(idOpt, attributeSetIdOpt, nameOpt, descriptionOpt)))
      }

      override def unbind(key: String, filter: AttributeValueSetFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.attributeSetIdOpt map (value => longBinder.unbind(key + ".attsetid", value)),
            filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
            filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[AttributeValueSet]] =
    Json.format[AttributeValueSetFilter].asInstanceOf[OFormat[ModelFilter[AttributeValueSet]]]
}
