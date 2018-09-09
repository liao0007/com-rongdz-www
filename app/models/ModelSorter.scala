package models

import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class ModelSorter(attribute: String = "id", order: String = "desc")

object ModelSorter {

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[ModelSorter] =
    new QueryStringBindable[ModelSorter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ModelSorter]] = {
        for {
          attribute <- stringBinder.bind(key + ".attribute", params)
          order <- stringBinder.bind(key + ".order", params)
        } yield {
          (attribute, order) match {
            case (Right(att), Right(ord)) => Right(ModelSorter(att, ord))
            case _ => Left("unable to bind a Sorter")
          }
        }
      }

      override def unbind(key: String, sorter: ModelSorter): String = {
        stringBinder.unbind(key + ".attribute", sorter.attribute) + "&" + stringBinder.unbind(key + ".order", sorter.order)
      }
    }

  implicit val format: OFormat[ModelSorter] = Json.format[ModelSorter]
}
