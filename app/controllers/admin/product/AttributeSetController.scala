package controllers.admin.product

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.product.{AttributeSet, AttributeSetFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.AttributeSetService

class AttributeSetController @Inject()(val messagesApi: MessagesApi,
                                       val silhouette: Silhouette[JWTEnv],
                                       val has: Has,
                                       val crudService: AttributeSetService)
    extends CrudController[AttributeSet] {

  override def indexJson(modelResult: ModelResult[AttributeSet])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.AttributeSetFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[AttributeSet])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.attributeSet.index(pagedSearchResult, pagination, filter.asInstanceOf[AttributeSetFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeSetController.index()
  }

  override def editHtml(form: Form[AttributeSet])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.product.attributeSet.edit(form, routes.AttributeSetController.update(record.id))
      case None         => views.html.admin.product.attributeSet.edit(form, routes.AttributeSetController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeSetController.edit(id)
  }

}
