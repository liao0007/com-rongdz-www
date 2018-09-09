package models.product.filters

import models.ModelFilter
import models.product.AttributeSet
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class AttributeSetFilter(idOpt: Option[Long] = None, nameOpt: Option[String] = None, descriptionOpt: Option[String] = None)
  extends ModelFilter[AttributeSet]

object AttributeSetFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[AttributeSetFilter] =
    new QueryStringBindable[AttributeSetFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AttributeSetFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
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

        Some(Right(AttributeSetFilter(idOpt, nameOpt, descriptionOpt)))
      }

      override def unbind(key: String, filter: AttributeSetFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
          filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[AttributeSet]] = Json.format[AttributeSetFilter].asInstanceOf[OFormat[ModelFilter[AttributeSet]]]
}
