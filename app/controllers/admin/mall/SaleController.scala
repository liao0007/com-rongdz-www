package controllers.admin.mall

import javax.inject.Inject
import auth.JWTEnv
import auth.services.authorizations.Has
import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import daos.default.user.ToPermission.UserToPermission
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import models.mall.{Sale, SaleFilter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.mall.SaleService
import com.github.aselab.activerecord.ActiveRecord.Relation1

import scala.concurrent.Future

class SaleController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: SaleService)
    extends CrudController[Sale] {

  override def indexJson(modelResult: ModelResult[Sale])(implicit requestHeader: RequestHeader): JsValue = {
    import models.mall.SaleFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Sale])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.mall.sale.index(pagedSearchResult, pagination, filter.asInstanceOf[SaleFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.SaleController.index()
  }

  override def editHtml(form: Form[Sale])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.mall.sale.edit(form, routes.SaleController.update(record.id))
      case None         => views.html.admin.mall.sale.edit(form, routes.SaleController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.SaleController.edit(id)
  }

  override def index(pager: ModelPager, filter: ModelFilter[Sale], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[Sale] = crudService.search(pager, filter, sorter, { relation: Relation1[Sale, Sale] =>
        relation.includes(_.brand, _.subcategory, _.category, _.sku)
      })
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }

}
