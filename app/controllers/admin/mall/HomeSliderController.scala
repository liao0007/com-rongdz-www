package controllers.admin.mall

import javax.inject.Inject
import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import models.ModelResult
import models.mall.{HomeSlider, HomeSliderFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.HomeSliderService

class HomeSliderController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: HomeSliderService) extends CrudController[HomeSlider] {

  override def indexJson(modelResult: ModelResult[HomeSlider])(implicit requestHeader: RequestHeader): JsValue = {
    import models.mall.HomeSliderFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[HomeSlider])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.homeSlider.index(pagedSearchResult, pagination, filter.asInstanceOf[HomeSliderFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.HomeSliderController.index()
  }

  override def editHtml(form: Form[HomeSlider])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.mall.homeSlider.edit(form, routes.HomeSliderController.update(record.id))
      case None         => views.html.admin.mall.homeSlider.edit(form, routes.HomeSliderController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.HomeSliderController.edit(id)
  }
}
