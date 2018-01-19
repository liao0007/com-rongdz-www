package controllers.admin.product

import javax.inject.Inject

import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.product.AttributeOption
import daos.default.user.ToPermission.UserToPermission
import models.ModelResult
import models.default.product.AttributeOptionFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.AttributeOptionService

import scala.concurrent.Future

class AttributeOptionController @Inject()(val messagesApi: MessagesApi,
                                          val silhouette: Silhouette[JWTEnv],
                                          val has: Has,
                                          val crudService: AttributeOptionService)
    extends CrudController[AttributeOption] {

  override def indexJson(modelResult: ModelResult[AttributeOption])(implicit requestHeader: RequestHeader): JsValue = {
    import models.default.product.AttributeOptionFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[AttributeOption])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.attributeOption.index(pagedSearchResult, pagination, filter.asInstanceOf[AttributeOptionFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeOptionController.index()
  }

  //for update only
  def editHtml(form: Form[AttributeOption])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.product.attributeOption.edit(record.attributeId, form, routes.AttributeOptionController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeOptionController.edit(id)
  }

  def newPage(attributeId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      Ok(views.html.admin.product.attributeOption.edit(attributeId, crudService.form(), routes.AttributeOptionController.create()))
    }
  }

  override def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.prev(id, None, Seq("attributeId" -> originalRecord.attributeId)) match {
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
          crudService.next(id, None, Seq("attributeId" -> originalRecord.attributeId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }

}
