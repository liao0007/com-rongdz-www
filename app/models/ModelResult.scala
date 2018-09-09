package models

import play.api.libs.json.{Json, _}

case class ModelResult[T <: models.ActiveRecord](records: Seq[T], pagination: Pagination, filter: ModelFilter[T], sorter: ModelSorter)

object ModelResult {
  implicit def format[T <: models.ActiveRecord](implicit recordFormat: Format[T], filterFormat: Format[ModelFilter[T]]): Format[ModelResult[T]] = new Format[ModelResult[T]] {
    override def reads(json: JsValue): JsResult[ModelResult[T]] =
      JsSuccess(
        new ModelResult(
          json \ "records" match {
            case JsDefined(JsArray(ts)) => ts.map {
              _.as[T](recordFormat)
            }
            case _ => throw new RuntimeException("records MUST be a list")
          },
          (json \ "pagination").as[Pagination],
          (json \ "filter").as[ModelFilter[T]](filterFormat),
          (json \ "sorter").as[ModelSorter]
        ))

    override def writes(ts: ModelResult[T]): JsValue =
      JsObject(
        Seq(
          "records" -> JsArray(ts.records.map(t => recordFormat.writes(t))),
          "pagination" -> Json.toJson(ts.pagination),
          "filter" -> filterFormat.writes(ts.filter),
          "sorter" -> Json.toJson(ts.sorter)
        ))
  }
}
