package controllers.admin.mall

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.mall.SaleRate
import models.mall.filters.SaleRateFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.SaleRateService

class SaleRateController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: SaleRateService)
    extends CrudController[SaleRate] {

  override def indexJson(modelResult: ModelResult[SaleRate])(implicit requestHeader: RequestHeader): JsValue = {
    import models.mall.SaleRateFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[SaleRate])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.saleRate.index(pagedSearchResult, pagination, filter.asInstanceOf[SaleRateFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.SaleRateController.index()
  }

  override def editHtml(form: Form[SaleRate])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.mall.saleRate.edit(form, routes.SaleRateController.update(record.id))
      case None         => views.html.admin.mall.saleRate.edit(form, routes.SaleRateController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.SaleRateController.edit(id)
  }

}
