package controllers.admin.mall

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.mall.SaleOrderDetailAttributeValue
import models.mall.filters.SaleOrderDetailAttributeValueFilter
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.SaleOrderDetailAttributeValueService

import scala.concurrent.Future

class SaleOrderDetailAttributeValueController @Inject()(val messagesApi: MessagesApi,
                                                        val silhouette: Silhouette[JWTEnv],
                                                        val has: Has,
                                                        val crudService: SaleOrderDetailAttributeValueService)
    extends CrudController[SaleOrderDetailAttributeValue] {

  override def indexJson(modelResult: ModelResult[SaleOrderDetailAttributeValue])(implicit requestHeader: RequestHeader): JsValue = {
    import models.mall.SaleOrderDetailAttributeValueFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[SaleOrderDetailAttributeValue])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.saleOrderDetailAttributeValue.index(pagedSearchResult, pagination, filter.asInstanceOf[SaleOrderDetailAttributeValueFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.SaleOrderDetailAttributeValueController.index()
  }

  override def editHtml(form: Form[SaleOrderDetailAttributeValue])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.mall.saleOrderDetailAttributeValue.edit(record.saleOrderDetailId, form, routes.SaleOrderDetailAttributeValueController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.SaleOrderDetailAttributeValueController.edit(id)
  }

  def newPage(saleOrderDetailAttributeValueId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      Ok(views.html.admin.mall.saleOrderDetailAttributeValue.edit(saleOrderDetailAttributeValueId, crudService.form(), routes.SaleOrderDetailAttributeValueController.create()))
    }
  }

  override def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.prev(id, None, Seq("saleOrderDetailId" -> originalRecord.saleOrderDetailId)) match {
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
          crudService.next(id, None, Seq("saleOrderDetailId" -> originalRecord.saleOrderDetailId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }

  override def index(pager: ModelPager, filter: ModelFilter[SaleOrderDetailAttributeValue], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[SaleOrderDetailAttributeValue] = crudService.search(pager, filter, sorter, { relation: Relation1[SaleOrderDetailAttributeValue, SaleOrderDetailAttributeValue] =>
        relation.includes(_.attribute)
      })
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }
}
