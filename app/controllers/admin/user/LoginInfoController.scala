package controllers.admin.user

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.user.{LoginInfo, LoginInfoFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.user.LoginInfoService

import scala.concurrent.Future

class LoginInfoController @Inject()(val messagesApi: MessagesApi,
                                    val silhouette: Silhouette[JWTEnv],
                                    val jWTAuthService: JWTAuthService,
                                    val has: Has,
                                    val crudService: LoginInfoService)
    extends CrudController[LoginInfo] {

  override def indexJson(modelResult: ModelResult[LoginInfo])(implicit requestHeader: RequestHeader): JsValue = {
    import models.user.LoginInfoFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[LoginInfo])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.user.loginInfo.index(pagedSearchResult, pagination, filter.asInstanceOf[LoginInfoFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.LoginInfoController.index()
  }

  override def editHtml(form: Form[LoginInfo])(implicit requestHeader: RequestHeader): Html = {
    val record = form.value.get
    views.html.admin.user.loginInfo.edit(record.userId, form, routes.LoginInfoController.update(record.id))
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.LoginInfoController.edit(id)
  }

  def newPage(userId: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    Future.successful {
      Ok(views.html.admin.user.loginInfo.edit(userId, crudService.form(), routes.LoginInfoController.create()))
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

  def resetPassword(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
    crudService.find(id) match {
      case Some(loginInfo) if loginInfo.providerId == LoginInfoProviderId.Credentials.toString =>
        val passwordUpdateRequest = PasswordUpdateRequest(loginInfo.providerKey, loginInfo.providerKey.takeRight(6))
        jWTAuthService.updatePassword(passwordUpdateRequest) map {
          case Right(_) =>
            Redirect(routes.LoginInfoController.edit(id)).flashing("success" -> "重置密码成功（手机后六位）")
          case _ =>
            Redirect(routes.LoginInfoController.edit(id)).flashing("danger" -> "重置密码失败")
        }

      case _ =>
        Future.successful {
          Redirect(routes.LoginInfoController.edit(id)).flashing("danger" -> "重置密码失败")
        }
    }

  }
}
