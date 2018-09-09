package controllers.admin.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models._
import models.product.Product
import models.product.filters.ProductFilter
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.ProductService

import scala.concurrent.Future

class ProductController @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[JWTEnv], val has: Has, val crudService: ProductService)
    extends CrudController[Product] {

  override def indexJson(modelResult: ModelResult[Product])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.ProductFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[Product])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.product.index(pagedSearchResult, pagination, filter.asInstanceOf[ProductFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.ProductController.index()
  }

  override def editHtml(form: Form[Product])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.product.product.edit(form, routes.ProductController.update(record.id))
      case None         => views.html.admin.product.product.edit(form, routes.ProductController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.ProductController.edit(id)
  }

  override def index(pager: ModelPager, filter: ModelFilter[Product], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[Product] = crudService.search(pager, filter, sorter, { relation: Relation1[Product, Product] =>
        relation.includes(_.brand, _.subcategory)
      })
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }
}
