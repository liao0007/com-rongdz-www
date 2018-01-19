package controllers.admin.mall

import javax.inject.Inject

import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.mall.SaleOrder
import models.ModelResult
import models.default.mall.SaleOrderFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.SaleOrderService

class SaleOrderController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: SaleOrderService)
    extends CrudController[SaleOrder] {

  override def indexJson(modelResult: ModelResult[SaleOrder])(implicit requestHeader: RequestHeader): JsValue = {
    import models.default.mall.SaleOrderFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[SaleOrder])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.saleOrder.index(pagedSearchResult, pagination, filter.asInstanceOf[SaleOrderFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.SaleOrderController.index()
  }

  override def editHtml(form: Form[SaleOrder])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.mall.saleOrder.edit(form, routes.SaleOrderController.update(record.id))
      case None         => views.html.admin.mall.saleOrder.edit(form, routes.SaleOrderController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.SaleOrderController.edit(id)
  }

}
