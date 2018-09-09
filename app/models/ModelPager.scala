package models

import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class ModelPager(page: Int = 1, size: Int = 24)

object ModelPager {
  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int]): QueryStringBindable[ModelPager] =
    new QueryStringBindable[ModelPager] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ModelPager]] = {
        for {
          indexEither <- intBinder.bind(key + ".page", params)
          sizeEither <- intBinder.bind(key + ".size", params)
        } yield {
          (indexEither, sizeEither) match {
            case (Right(page), Right(size)) => Right(ModelPager(page, size))
            case _ => Left("Unable to bind a Pager")
          }
        }
      }

      override def unbind(key: String, pager: ModelPager): String = {
        intBinder.unbind(key + ".page", pager.page) + "&" + intBinder.unbind(key + ".size", pager.size)
      }
    }

  implicit val format: OFormat[ModelPager] = Json.format[ModelPager]
}

case class Pagination(pager: ModelPager, totalPages: Int, totalRecord: Int, recordOffset: Int)

object Pagination {
  implicit val format: OFormat[Pagination] = Json.format[Pagination]

  def apply(pager: ModelPager, totalItems: Int): Pagination = {
    val from: Int = (pager.page - 1) * pager.size
    val totalPages: Int = (totalItems / pager.size) + (if (totalItems % pager.size > 0) 1 else 0)
    Pagination(pager, totalPages, totalItems, from)
  }
}