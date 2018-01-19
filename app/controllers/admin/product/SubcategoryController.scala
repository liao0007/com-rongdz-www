package controllers.admin.product

import javax.inject.Inject

import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.product.Subcategory
import models.ModelResult
import models.default.mall.HomeFeatureFilter
import models.default.product.SubcategoryFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.SubcategoryService

class SubcategoryController @Inject()(val messagesApi: MessagesApi,
                                      val silhouette: Silhouette[JWTEnv],
                                      val has: Has,
                                      val crudService: SubcategoryService)
    extends CrudController[Subcategory] {

  override def indexJson(modelResult: ModelResult[Subcategory])(implicit requestHeader: RequestHeader): JsValue = {
    import models.default.product.SubcategoryFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Subcategory])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.subcategory.index(pagedSearchResult, pagination, filter.asInstanceOf[SubcategoryFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.SubcategoryController.index()
  }

  override def editHtml(form: Form[Subcategory])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.product.subcategory.edit(form, routes.SubcategoryController.update(record.id))
      case None         => views.html.admin.product.subcategory.edit(form, routes.SubcategoryController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.SubcategoryController.edit(id)
  }
}
