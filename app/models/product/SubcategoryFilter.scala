package models.product

import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class SubcategoryFilter(idOpt: Option[Long] = None,
                             categoryIdOpt: Option[Long] = None,
                             nameOpt: Option[String] = None,
                             descriptionOpt: Option[String] = None)
    extends ModelFilter[Subcategory]

object SubcategoryFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[SubcategoryFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SubcategoryFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val categoryIdOpt = longBinder.bind(key + ".cid", params).flatMap {
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

        Some(Right(SubcategoryFilter(idOpt, categoryIdOpt, nameOpt, descriptionOpt)))
      }

      override def unbind(key: String, filter: SubcategoryFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.categoryIdOpt map (value => longBinder.unbind(key + ".cid", value)),
            filter.nameOpt map (value => stringBinder.unbind(key + ".name", value)),
            filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Subcategory]] = Json.format[SubcategoryFilter].asInstanceOf[OFormat[ModelFilter[Subcategory]]]
}
