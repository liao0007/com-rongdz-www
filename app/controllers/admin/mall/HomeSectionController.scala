package controllers.admin.mall

import javax.inject.Inject

import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.mall.HomeSection
import models.ModelResult
import models.default.mall.HomeSectionFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.HomeSectionService

class HomeSectionController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: HomeSectionService) extends CrudController[HomeSection] {

  override def indexJson(modelResult: ModelResult[HomeSection])(implicit requestHeader: RequestHeader): JsValue = {
    import models.default.mall.HomeSectionFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[HomeSection])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.homeSection.index(pagedSearchResult, pagination, filter.asInstanceOf[HomeSectionFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.HomeSectionController.index()
  }

  override def editHtml(form: Form[HomeSection])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.mall.homeSection.edit(form, routes.HomeSectionController.update(record.id))
      case None         => views.html.admin.mall.homeSection.edit(form, routes.HomeSectionController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.HomeSectionController.edit(id)
  }
}
