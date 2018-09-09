package controllers

import javax.inject.Inject
import play.api.Environment
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

class UploadedAssets @Inject()(val messagesApi: MessagesApi,
                               environment: Environment)
    extends Controller
    with I18nSupport {

  def at(file: String) = Action { implicit request =>
    Ok.sendFile(new java.io.File(environment.rootPath + "/uploaded/" + file))
  }
}
