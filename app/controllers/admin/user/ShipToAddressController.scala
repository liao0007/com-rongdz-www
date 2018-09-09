package controllers.admin.user

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.user.{ShipToAddress, ShipToAddressFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.user.ShipToAddressService

import scala.concurrent.Future

class ShipToAddressController @Inject()(val messagesApi: MessagesApi,
                                        val silhouette: Silhouette[JWTEnv],
                                        val has: Has,
                                        val crudService: ShipToAddressService)
    extends CrudController[ShipToAddress] {

  override def indexJson(modelResult: ModelResult[ShipToAddress])(implicit requestHeader: RequestHeader): JsValue = {
    import models.user.ShipToAddressFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[ShipToAddress])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.user.shipToAddress.index(pagedSearchResult, pagination, filter.asInstanceOf[ShipToAddressFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.ShipToAddressController.index()
  }

  override def editHtml(form: Form[ShipToAddress])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.user.shipToAddress.edit(record.userId, form, routes.ShipToAddressController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.ShipToAddressController.edit(id)
  }

  def newPage(userId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      Ok(views.html.admin.user.shipToAddress.edit(userId, crudService.form(), routes.ShipToAddressController.create()))
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
