package models.product.filters

import models.ModelFilter
import models.product.Attribute
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class AttributeFilter(idOpt: Option[Long] = None,
                           nameOpt: Option[String] = None) extends ModelFilter[Attribute]

object AttributeFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[AttributeFilter] =
    new QueryStringBindable[AttributeFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AttributeFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val nameOpt = stringBinder.bind(key + ".name", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _ => None
        }

        Some(Right(AttributeFilter(idOpt, nameOpt)))
      }

      override def unbind(key: String, filter: AttributeFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.nameOpt map (value => stringBinder.unbind(key + ".name", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Attribute]] = Json.format[AttributeFilter].asInstanceOf[OFormat[ModelFilter[Attribute]]]
}
