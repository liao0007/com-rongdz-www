package controllers.admin.product

import javax.inject.Inject
import auth.JWTEnv
import auth.services.authorizations.Has
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.product.AttributeValue
import daos.default.user.ToPermission.UserToPermission
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import models.mall.{HomeFeatureFilter, SaleOrderDetailAttributeValue}
import models.product.{AttributeSetDetail, AttributeSetDetailFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.AttributeSetDetailService
import com.github.aselab.activerecord.ActiveRecord.Relation1

import scala.concurrent.Future

class AttributeSetDetailController @Inject()(val messagesApi: MessagesApi,
                                          val silhouette: Silhouette[JWTEnv],
                                          val has: Has,
                                          val crudService: AttributeSetDetailService)
  extends CrudController[AttributeSetDetail] {

  override def indexJson(modelResult: ModelResult[AttributeSetDetail])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.AttributeSetDetailFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[AttributeSetDetail])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.attributeSetDetail.index(pagedSearchResult, pagination, filter.asInstanceOf[AttributeSetDetailFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeSetDetailController.index()
  }

  //for update only
  override def editHtml(form: Form[AttributeSetDetail])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.product.attributeSetDetail.edit(record.attributeSetId, form, routes.AttributeSetDetailController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeSetDetailController.edit(id)
  }

  def newPage(attributeSetId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful { Ok(views.html.admin.product.attributeSetDetail.edit(attributeSetId, crudService.form(), routes.AttributeSetDetailController.create())) }
  }

  override def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.prev(id, None, Seq("attributeSetId" -> originalRecord.attributeSetId)) match {
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
          crudService.next(id, None, Seq("attributeSetId" -> originalRecord.attributeSetId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }

  override def index(pager: ModelPager, filter: ModelFilter[AttributeSetDetail], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[AttributeSetDetail] = crudService.search(pager, filter, sorter, { relation: Relation1[AttributeSetDetail, AttributeSetDetail] =>
        relation.includes(_.attribute)
      })
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }

}
