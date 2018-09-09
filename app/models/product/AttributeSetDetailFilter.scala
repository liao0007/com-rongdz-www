package models.product

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class AttributeSetDetailFilter(idOpt: Option[Long] = None,
                                    attributeSetIdOpt: Option[Long] = None,
                                    attributeIdOpt: Option[Long] = None,
                                    sequenceOpt: Option[Long] = None)
  extends ModelFilter[AttributeSetDetail]

object AttributeSetDetailFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[AttributeSetDetailFilter] =
    new QueryStringBindable[AttributeSetDetailFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AttributeSetDetailFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val attributeSetIdOpt = longBinder.bind(key + ".attsetid", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val attributeIdOpt = longBinder.bind(key + ".attid", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        val sequenceOpt = longBinder.bind(key + ".seq", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        Some(Right(AttributeSetDetailFilter(idOpt, attributeSetIdOpt, attributeIdOpt, sequenceOpt)))
      }

      override def unbind(key: String, filter: AttributeSetDetailFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.attributeSetIdOpt map (value => longBinder.unbind(key + ".attsetid", value)),
          filter.attributeIdOpt map (value => longBinder.unbind(key + ".attid", value)),
          filter.sequenceOpt map (value => longBinder.unbind(key + ".seq", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[AttributeSetDetail]] =
    Json.format[AttributeSetDetailFilter].asInstanceOf[OFormat[ModelFilter[AttributeSetDetail]]]
}
