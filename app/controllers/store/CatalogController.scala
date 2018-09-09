package controllers.store

import auth.filters.CookieSettings
import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import models.mall.{Sale, SaleFilter}
import models.product.Subcategory
import models.{ModelPager, ModelSorter}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.mall.SaleService

import scala.concurrent.Future

class CatalogController @Inject()(val messagesApi: MessagesApi,
                                  silhouette: Silhouette[JWTEnv],
                                  cookieSettings: CookieSettings,
                                  saleService: SaleService)
    extends Controller
    with I18nSupport
    with Paging {

  val includes = { relation: Relation1[Sale, Sale] =>
    relation.includes(_.category, _.subcategory, _.sku, _.product)
  }
  def list(pager: ModelPager, saleFilter: SaleFilter, saleSorter: ModelSorter): Action[AnyContent] = silhouette.UserAwareAction.async {
    implicit request =>
      Future.successful {
        val subcategoryIds = saleService.processFilter(Sale.all, saleFilter.copy(activeOpt = Some(true))).select(_.subcategoryId).distinct.toSeq
        val subcategories  = Subcategory.where(_.id in subcategoryIds).orderBy("sequence", "DESC").toSeq

        if (subcategories.length > 1) {
          val result = subcategories map { subcategory =>
            val modelResult =
              saleService.search(ModelPager(size = 8), saleFilter.copy(subcategoryIdOpt = Some(subcategory.id), activeOpt = Some(true)), saleSorter, includes)
            subcategory -> modelResult.records
          }
          Ok(views.html.store.catalog.list(result, None, saleFilter.asInstanceOf[SaleFilter], saleSorter))
        } else if (subcategories.length == 1) {

          val subcategory = subcategories.head
          val modelResult = saleService.search(pager, saleFilter.copy(activeOpt = Some(true)), saleSorter, includes)

          Ok(views.html.store.catalog.list(Seq(subcategory -> modelResult.records), Some(modelResult.pagination), saleFilter, saleSorter))
        } else {
          Redirect(controllers.store.routes.ApplicationController.index())
        }
      }
  }

  def sale(saleNumber: String, pager: ModelPager, saleFilter: SaleFilter, saleSorter: ModelSorter): Action[AnyContent] =
    silhouette.UserAwareAction.async { implicit request =>
      Future.successful {
        Sale.findBy("saleNumber" -> saleNumber) match {
          case Some(sale) => Ok(views.html.store.catalog.sale(sale, pager, saleFilter, saleSorter))
          case _          => Redirect(controllers.store.routes.ApplicationController.index())
        }
      }
    }

}
