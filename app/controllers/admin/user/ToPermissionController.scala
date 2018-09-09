package controllers.admin.user

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.user.Permission
import models.user.filters.PermissionFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.user.ToPermissionService

import scala.concurrent.Future

class ToPermissionController @Inject()(val messagesApi: MessagesApi,
                                       val silhouette: Silhouette[JWTEnv],
                                       val has: Has,
                                       val crudService: ToPermissionService)
    extends CrudController[Permission] {

  override def indexJson(modelResult: ModelResult[Permission])(implicit requestHeader: RequestHeader): JsValue = {
    import models.user.PermissionFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Permission])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.user.toPermission.index(pagedSearchResult, pagination, filter.asInstanceOf[PermissionFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.ToPermissionController.index()
  }

  override def editHtml(form: Form[Permission])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.user.toPermission.edit(record.userId, form, routes.ToPermissionController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.ToPermissionController.edit(id)
  }

  def newPage(userId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      Ok(views.html.admin.user.toPermission.edit(userId, crudService.form(), routes.ToPermissionController.create()))
    }
  }

  override def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.prev(id, None, Seq("userId" -> originalRecord.userId)) match {
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
          crudService.next(id, None, Seq("userId" -> originalRecord.userId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }
}
