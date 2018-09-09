package controllers.admin.crm

import javax.inject.Inject
import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import models._
import models.crm.{Page, PageFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.crm.PageService

class PageController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: PageService) extends CrudController[Page] {

  override def indexJson(modelResult: ModelResult[Page])(implicit requestHeader: RequestHeader): JsValue = {
    import models.crm.PageFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Page])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.crm.page.index(pagedSearchResult, pagination, filter.asInstanceOf[PageFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.PageController.index()
  }

  override def editHtml(form: Form[Page])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.crm.page.edit(form, routes.PageController.update(record.id))
      case None         => views.html.admin.crm.page.edit(form, routes.PageController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.PageController.edit(id)
  }
}
