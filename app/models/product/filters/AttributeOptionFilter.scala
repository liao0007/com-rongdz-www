package models.product.filters

import models.ModelFilter
import models.product.AttributeOption
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class AttributeOptionFilter(idOpt: Option[Long] = None,
                                 attributeIdOpt: Option[Long] = None,
                                 valueOpt: Option[String] = None,
                                 nameOpt: Option[String] = None,
                                 descriptionOpt: Option[String] = None)
  extends ModelFilter[AttributeOption]

object AttributeOptionFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[AttributeOptionFilter] =
    new QueryStringBindable[AttributeOptionFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AttributeOptionFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val attributeIdOpt = longBinder.bind(key + ".attid", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val valueOpt = stringBinder.bind(key + ".value", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        val descriptionOpt = stringBinder.bind(key + ".desc", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(Right(AttributeOptionFilter(idOpt, attributeIdOpt, valueOpt, nameOpt, descriptionOpt)))
      }

      override def unbind(key: String, filter: AttributeOptionFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.attributeIdOpt map (value => longBinder.unbind(key + ".attid", value)),
          filter.valueOpt map (value => stringBinder.unbind(key + ".value", value)),
          filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
          filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[AttributeOption]] = Json.format[AttributeOptionFilter].asInstanceOf[OFormat[ModelFilter[AttributeOption]]]
}
