package controllers.admin.mall

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.mall.{HomeFeature, HomeFeatureFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.HomeFeatureService

class HomeFeatureController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: HomeFeatureService) extends CrudController[HomeFeature] {

  override def indexJson(modelResult: ModelResult[HomeFeature])(implicit requestHeader: RequestHeader): JsValue = {
    import models.mall.HomeFeatureFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[HomeFeature])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.homeFeature.index(pagedSearchResult, pagination, filter.asInstanceOf[HomeFeatureFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.HomeFeatureController.index()
  }

  override def editHtml(form: Form[HomeFeature])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.mall.homeFeature.edit(form, routes.HomeFeatureController.update(record.id))
      case None         => views.html.admin.mall.homeFeature.edit(form, routes.HomeFeatureController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.HomeFeatureController.edit(id)
  }
}
