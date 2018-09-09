package controllers.admin.mall

import javax.inject.Inject
import auth.JWTEnv
import auth.services.authorizations.Has
import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.user.ToPermission.UserToPermission
import models.mall.{BookingFollowup, BookingFollowupFilter}
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.BookingFollowupService

import scala.concurrent.Future

class BookingFollowupController @Inject()(val messagesApi: MessagesApi,
                                          val silhouette: Silhouette[JWTEnv],
                                          val has: Has,
                                          val crudService: BookingFollowupService)
    extends CrudController[BookingFollowup] {

  override def indexJson(modelResult: ModelResult[BookingFollowup])(implicit requestHeader: RequestHeader): JsValue = {
    import models.mall.BookingFollowupFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[BookingFollowup])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.bookingFollowup.index(pagedSearchResult, pagination, filter.asInstanceOf[BookingFollowupFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.BookingFollowupController.index()
  }

  override def editHtml(form: Form[BookingFollowup])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.mall.bookingFollowup.edit(record.bookingId, form, routes.BookingFollowupController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.BookingFollowupController.edit(id)
  }

  def newPage(bookingId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      Ok(views.html.admin.mall.bookingFollowup.edit(bookingId, crudService.form(), routes.BookingFollowupController.create()))
    }
  }

  override def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.prev(id, None, Seq("bookingId" -> originalRecord.bookingId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是第一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }

  override def next(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.next(id, None, Seq("bookingId" -> originalRecord.bookingId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }
}
