package controllers.admin.product

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.product.Brand
import models.product.filters.BrandFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.BrandService

class BrandController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: BrandService)
    extends CrudController[Brand] {

  override def indexJson(modelResult: ModelResult[Brand])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.BrandFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Brand])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.brand.index(pagedSearchResult, pagination, filter.asInstanceOf[BrandFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.BrandController.index()
  }

  override def editHtml(form: Form[Brand])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.product.brand.edit(form, routes.BrandController.update(record.id))
      case None         => views.html.admin.product.brand.edit(form, routes.BrandController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.BrandController.edit(id)
  }

}
