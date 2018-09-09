package controllers.admin

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

class ApplicationController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("AdminRoutes")(
        controllers.admin.core.routes.javascript.ImageController.index,

        controllers.admin.user.routes.javascript.UserController.index,

        controllers.admin.product.routes.javascript.CategoryController.index,
        controllers.admin.product.routes.javascript.SubcategoryController.index,
        controllers.admin.product.routes.javascript.BrandController.index,
        controllers.admin.product.routes.javascript.ProductController.index,
        controllers.admin.product.routes.javascript.SkuController.index,

        controllers.admin.product.routes.javascript.AttributeController.index,
        controllers.admin.product.routes.javascript.AttributeOptionController.index,
        controllers.admin.product.routes.javascript.AttributeSetController.index,
        controllers.admin.product.routes.javascript.AttributeSetDetailController.index,
        controllers.admin.product.routes.javascript.AttributeValueController.index,
        controllers.admin.product.routes.javascript.AttributeValueSetController.index,

        controllers.admin.mall.routes.javascript.BookingController.index,
        controllers.admin.mall.routes.javascript.SaleController.index,
        controllers.admin.mall.routes.javascript.HomeFeatureController.index,
        controllers.admin.mall.routes.javascript.HomeSectionController.index,
        controllers.admin.mall.routes.javascript.HomeSliderController.index
      )
    ).as("text/javascript")
  }

  def index() = Action { implicit request =>
    Ok(views.html.admin.index())
  }

}
