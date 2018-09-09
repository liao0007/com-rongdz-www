package controllers.store

import javax.inject.Inject
import auth.JWTEnv
import com.mohiva.play.silhouette.api.Silhouette
import models.Paging
import models.crm.Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.Future

class PageController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[JWTEnv]) extends Controller with I18nSupport with Paging {

  def guide(id: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      id.toLowerCase match {
        case "suit"  => Ok(views.html.store.page.guide.suit())
        case "shirt" => Ok(views.html.store.page.guide.shirt())
        case _       => Redirect(controllers.store.routes.ApplicationController.index())
      }
    }
  }

  def info(id: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      id.toLowerCase match {
        case "faq"    => Ok(views.html.store.page.info.faq())
        case "terms"  => Ok(views.html.store.page.info.terms())
        case "career" => Ok(views.html.store.page.info.career())
        case _        => Redirect(controllers.store.routes.ApplicationController.index())
      }
    }
  }

  def page(id: Long): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Page.find(id) match {
        case Some(page) => Ok(views.html.store.page.view(page))
        case _          => Redirect(controllers.store.routes.ApplicationController.index())
      }
    }
  }

}
