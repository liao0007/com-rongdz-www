package controllers.admin.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.product.AttributeValue
import models.product.filters.AttributeValueFilter
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.AttributeValueService

import scala.concurrent.Future

class AttributeValueController @Inject()(val messagesApi: MessagesApi,
                                         val silhouette: Silhouette[JWTEnv],
                                         val has: Has,
                                         val crudService: AttributeValueService)
    extends CrudController[AttributeValue] {

  override def indexJson(modelResult: ModelResult[AttributeValue])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.AttributeValueFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[AttributeValue])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.attributeValue.index(pagedSearchResult, pagination, filter.asInstanceOf[AttributeValueFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeValueController.index()
  }

  //for update only
  override def editHtml(form: Form[AttributeValue])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.product.attributeValue.edit(record.attributeValueSetId, form, routes.AttributeValueController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeValueController.edit(id)
  }

  def newPage(attributeValueSetId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async {
    implicit request =>
      Future.successful {
        Ok(views.html.admin.product.attributeValue.edit(attributeValueSetId, crudService.form(), routes.AttributeValueController.create()))
      }
  }

  override def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case Some(originalRecord) =>
          crudService.prev(id, None, Seq("attributeValueSetId" -> originalRecord.attributeValueSetId)) match {
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
          crudService.next(id, None, Seq("attributeValueSetId" -> originalRecord.attributeValueSetId)) match {
            case Some(record) => Redirect(editCall(record.id))
            case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
          }
        case None => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }

  override def index(pager: ModelPager, filter: ModelFilter[AttributeValue], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[AttributeValue] = crudService.search(pager, filter, sorter, { relation: Relation1[AttributeValue, AttributeValue] =>
        relation.includes(_.attribute)
      })
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }

}
