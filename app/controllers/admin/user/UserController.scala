package controllers.admin.user

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.user.{User, UserFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.user.UserService

class UserController @Inject()(val messagesApi: MessagesApi,
                               val silhouette: Silhouette[JWTEnv],
                               val has: Has,
                               val crudService: UserService)
    extends CrudController[User] {

  override def indexJson(modelResult: ModelResult[User])(implicit requestHeader: RequestHeader): JsValue = {
    import models.user.UserFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[User])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.user.user.index(pagedSearchResult, pagination, filter.asInstanceOf[UserFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.UserController.index()
  }

  override def editHtml(form: Form[User])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.user.user.edit(form, routes.UserController.update(record.id))
      case None         => views.html.admin.user.user.edit(form, routes.UserController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.UserController.edit(id)
  }
}
