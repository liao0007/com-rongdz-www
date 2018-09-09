package models.product

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class CategoryFilter(idOpt: Option[Long] = None,
                          nameOpt: Option[String] = None,
                          descriptionOpt: Option[String] = None,
                          isCustomOpt: Option[Boolean] = None) extends ModelFilter[Category]

object CategoryFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[CategoryFilter] =
    new QueryStringBindable[CategoryFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CategoryFilter]] = {
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

        val isCustomOpt = booleanBinder.bind(key + ".cust", params).flatMap {
          case Right(value) => Some(value)
          case _ => None
        }

        Some(Right(CategoryFilter(idOpt, nameOpt, descriptionOpt, isCustomOpt)))
      }

      override def unbind(key: String, filter: CategoryFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
          filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
          filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value)),
          filter.isCustomOpt map (value => booleanBinder.unbind(key + ".cust", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Category]] = Json.format[CategoryFilter].asInstanceOf[OFormat[ModelFilter[Category]]]
}
