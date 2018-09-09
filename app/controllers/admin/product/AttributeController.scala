package controllers.admin.product

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.product.Attribute
import models.product.filters.AttributeFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.AttributeService

class AttributeController @Inject()(val messagesApi: MessagesApi,
                                    val silhouette: Silhouette[JWTEnv],
                                    val has: Has,
                                    val crudService: AttributeService)
    extends CrudController[Attribute] {

  override def indexJson(modelResult: ModelResult[Attribute])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.AttributeFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Attribute])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.attribute.index(pagedSearchResult, pagination, filter.asInstanceOf[AttributeFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeController.index()
  }

  override def editHtml(form: Form[Attribute])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.product.attribute.edit(form, routes.AttributeController.update(record.id))
      case None         => views.html.admin.product.attribute.edit(form, routes.AttributeController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeController.edit(id)
  }
}
