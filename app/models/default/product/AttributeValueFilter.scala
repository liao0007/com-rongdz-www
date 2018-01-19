package models.default.product

import daos.default.product.AttributeValue
import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class AttributeValueFilter(idOpt: Option[Long] = None,
                                attributeValueSetIdOpt: Option[Long] = None,
                                attributeIdOpt: Option[Long] = None,
                                valueOpt: Option[String] = None)
    extends ModelFilter[AttributeValue]

object AttributeValueFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[AttributeValueFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AttributeValueFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val attributeValueSetIdOpt = longBinder.bind(key + ".attvsetid", params).flatMap {
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

        Some(Right(AttributeValueFilter(idOpt, attributeValueSetIdOpt, attributeIdOpt, valueOpt)))
      }

      override def unbind(key: String, filter: AttributeValueFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.attributeValueSetIdOpt map (value => longBinder.unbind(key + ".attvsetid", value)),
            filter.attributeIdOpt map (value => longBinder.unbind(key + ".attid", value)),
            filter.valueOpt map (value => stringBinder.unbind(key + ".value", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[AttributeValue]] = Json.format[AttributeValueFilter].asInstanceOf[OFormat[ModelFilter[AttributeValue]]]
}
