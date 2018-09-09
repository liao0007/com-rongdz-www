package controllers.admin.core

import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.ModelResult
import models.core.Image
import models.core.filters.ImageFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.Files.TemporaryFile
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.core.ImageService

class ImageController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: ImageService)
    extends CrudController[Image] {

  override def indexJson(modelResult: ModelResult[Image])(implicit requestHeader: RequestHeader): JsValue = {
    import models.core.ImageFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Image])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.core.image.index(pagedSearchResult, pagination, filter.asInstanceOf[ImageFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.ImageController.index()
  }

  override def editHtml(form: Form[Image])(implicit requestHeader: RequestHeader): Html = ???

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = ???

  def upload(): Action[MultipartFormData[TemporaryFile]] =
    Action(parse.multipartFormData) { request =>
      request.body.file("file").fold(NotFound) { file =>
        crudService.upload(file.ref, file.contentType.get)
        Ok
      }
    }

}
