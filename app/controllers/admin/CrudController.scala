package controllers.admin

import com.mohiva.play.silhouette.api.Silhouette
import models.{ActiveRecord, _}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html
import services.CrudService

import scala.concurrent.Future

abstract class CrudController[T <: ActiveRecord] extends Controller with I18nSupport with Paging {

  val silhouette: Silhouette[JWTEnv]
  val has: Has
  val crudService: CrudService[T]

  def indexJson(modelResult: ModelResult[T])(implicit requestHeader: RequestHeader): JsValue
  def indexHtml(modelResult: ModelResult[T])(implicit requestHeader: RequestHeader): Html
  def indexCall(implicit requestHeader: RequestHeader): Call
  def editHtml(form: Form[T])(implicit requestHeader: RequestHeader): Html
  def editCall(id: Long)(implicit requestHeader: RequestHeader): Call

  def index(pager: ModelPager, filter: ModelFilter[T], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[T] = crudService.search(pager, filter, sorter)
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }

  def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.prev(id, None) match {
        case Some(record) => Redirect(editCall(record.id))
        case _            => Redirect(editCall(id)).flashing("danger" -> "已经是第一条记录")
      }
    }
  }

  def next(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.next(id, None) match {
        case Some(record) => Redirect(editCall(record.id))
        case _            => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
      }
    }
  }

  def newPage(): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful { Ok(editHtml(crudService.form())) }
  }

  def create(): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService
        .form()
        .bindFromRequest
        .fold(
          errors => BadRequest(editHtml(errors)), { record =>
            val createdRecord = crudService.create(record)
            Redirect(editCall(createdRecord.id))
          }
        )
    }
  }

  def edit(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    crudService.find(id) match {
      case recordOpt @ Some(_) => Future.successful { Ok(editHtml(crudService.form(recordOpt))) }
      case _                   => Future.successful { Redirect(indexCall).flashing("danger" -> "记录不存在") }
    }
  }

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      crudService.find(id) match {
        case recordOpt @ Some(_) =>
          crudService
            .form(recordOpt)
            .bindFromRequest
            .fold(
              errors => BadRequest(editHtml(errors)), { record =>
                val updatedRecord = crudService.update(record)
                Redirect(editCall(updatedRecord.id))
              }
            )
        case _ => Redirect(indexCall).flashing("danger" -> "记录不存在")
      }
    }
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async {
    Future.successful {
      crudService.find(id) match {
        case Some(record) =>
          crudService.delete(record)
          Ok
        case _ => NotFound
      }
    }
  }

}
