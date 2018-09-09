package controllers.admin.product

import javax.inject.Inject
import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import models.ModelResult
import models.product.{Category, CategoryFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.CategoryService

class CategoryController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: CategoryService)
    extends CrudController[Category] {

  override def indexJson(modelResult: ModelResult[Category])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.CategoryFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Category])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.category.index(pagedSearchResult, pagination, filter.asInstanceOf[CategoryFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.CategoryController.index()
  }

  override def editHtml(form: Form[Category])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.product.category.edit(form, routes.CategoryController.update(record.id))
      case None         => views.html.admin.product.category.edit(form, routes.CategoryController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.CategoryController.edit(id)
  }
}
