package controllers.admin.mall

import javax.inject.Inject
import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import models._
import models.mall.{Booking, BookingFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.BookingService

class BookingController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: BookingService) extends CrudController[Booking] {

  override def indexJson(modelResult: ModelResult[Booking])(implicit requestHeader: RequestHeader): JsValue = {
    import models.mall.BookingFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Booking])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.booking.index(pagedSearchResult, pagination, filter.asInstanceOf[BookingFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.BookingController.index()
  }

  override def editHtml(form: Form[Booking])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.mall.booking.edit(form, routes.BookingController.update(record.id))
      case None         => views.html.admin.mall.booking.edit(form, routes.BookingController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.BookingController.edit(id)
  }
}
