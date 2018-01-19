package controllers.admin.mall

import javax.inject.Inject

import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.mall.{Sale, SaleOrderDetail}
import daos.default.user.ToPermission.UserToPermission
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import models.default.mall.SaleOrderDetailFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.SaleOrderDetailService
import com.github.aselab.activerecord.ActiveRecord.Relation1

import scala.concurrent.Future

class SaleOrderDetailController @Inject()(val messagesApi: MessagesApi,
                                          val silhouette: Silhouette[JWTEnv],
                                          val has: Has,
                                          val crudService: SaleOrderDetailService)
    extends CrudController[SaleOrderDetail] {

  override def indexJson(modelResult: ModelResult[SaleOrderDetail])(implicit requestHeader: RequestHeader): JsValue = {
    import models.default.mall.SaleOrderDetailFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[SaleOrderDetail])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.saleOrderDetail.index(pagedSearchResult, pagination, filter.asInstanceOf[SaleOrderDetailFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.SaleOrderDetailController.index()
  }

  override def editHtml(form: Form[SaleOrderDetail])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.mall.saleOrderDetail.edit(record.saleOrderId, form, routes.SaleOrderDetailController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.SaleOrderDetailController.edit(id)
  }

  def newPage(saleOrderId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      Ok(views.html.admin.mall.saleOrderDetail.edit(saleOrderId, crudService.form(), routes.SaleOrderDetailController.create()))
    }
  }

  override def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.prev(id, None, Seq("saleOrderId" -> originalRecord.saleOrderId)) match {
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
          crudService.next(id, None, Seq("saleOrderId" -> originalRecord.saleOrderId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }

  override def index(pager: ModelPager, filter: ModelFilter[SaleOrderDetail], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[SaleOrderDetail] = crudService.search(pager, filter, sorter, { relation: Relation1[SaleOrderDetail, SaleOrderDetail] =>
        relation.includes(_.sale, _.saleOrder, _.saleOrderDetailAttributeValues)
      })
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }
}
