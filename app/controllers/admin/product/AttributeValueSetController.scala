package controllers.admin.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.mohiva.play.silhouette.api.Silhouette
import controllers.admin.CrudController
import javax.inject.Inject
import models.product.AttributeValueSet
import models.product.filters.AttributeValueSetFilter
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import services.product.AttributeValueSetService

import scala.concurrent.Future

class AttributeValueSetController @Inject()(val messagesApi: MessagesApi,
                                            val silhouette: Silhouette[JWTEnv],
                                            val has: Has,
                                            val crudService: AttributeValueSetService)
    extends CrudController[AttributeValueSet] {

  override def indexJson(modelResult: ModelResult[AttributeValueSet])(implicit requestHeader: RequestHeader): JsValue = {
    import models.product.AttributeValueSetFilter.format
    Json.toJson(modelResult)
  }

  override def indexHtml(modelResult: ModelResult[AttributeValueSet])(implicit requestHeader: RequestHeader): Html = {
    val ModelResult(pagedSearchResult, pagination, filter, sorter) = modelResult
    views.html.admin.product.attributeValueSet.index(pagedSearchResult, pagination, filter.asInstanceOf[AttributeValueSetFilter], sorter)
  }

  override def indexCall(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeValueSetController.index()
  }

  override def editHtml(form: Form[AttributeValueSet])(implicit requestHeader: RequestHeader): Html = {
    form.value match {
      case Some(record) => views.html.admin.product.attributeValueSet.edit(form, routes.AttributeValueSetController.update(record.id))
      case None         => views.html.admin.product.attributeValueSet.edit(form, routes.AttributeValueSetController.create())
    }
  }

  override def editCall(id: Long)(implicit requestHeader: RequestHeader): Call = {
    routes.AttributeValueSetController.edit(id)
  }

  override def index(pager: ModelPager, filter: ModelFilter[AttributeValueSet], sorter: ModelSorter): Action[AnyContent] =
    silhouette.SecuredAction(has.permission(UserToPermission.Admin)).async { implicit request =>
      val modelResult: ModelResult[AttributeValueSet] = crudService.search(pager, filter, sorter, { relation: Relation1[AttributeValueSet, AttributeValueSet] =>
        relation.includes(_.attributeValues, _.attributeSet)
      })
      render.async {
        case Accepts.Json() => Future.successful { Ok(indexJson(modelResult)) }
        case _              => Future.successful { Ok(indexHtml(modelResult)) }
      }
    }

}
