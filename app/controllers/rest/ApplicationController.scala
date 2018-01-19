package controllers.rest

import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

/**
  * Created by liangliao on 7/14/16.
  */
class ApplicationController extends Controller {

  // handel cross-origin-resource-shareing check
  def cors(resource: String) = Action { implicit request =>
    Ok
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("RestRoutes")(
        controllers.rest.routes.javascript.AuthController.signIn,
        controllers.rest.routes.javascript.AuthController.signUp,
        controllers.rest.routes.javascript.AuthController.socialBind,
        controllers.rest.routes.javascript.AuthController.socialSignUp,
        controllers.rest.routes.javascript.AuthController.changePassword,
        controllers.rest.routes.javascript.AuthController.requestSmsResetPasswordToken,
        controllers.rest.routes.javascript.AuthController.execute,

        controllers.rest.routes.javascript.UserController.get,
        controllers.rest.routes.javascript.UserController.update,

        controllers.rest.routes.javascript.CartController.update,
        controllers.rest.routes.javascript.CartController.added,
        controllers.rest.routes.javascript.CartController.subtotal,
        controllers.rest.routes.javascript.CartController.count,
        controllers.rest.routes.javascript.CartController.miniView,

        controllers.rest.routes.javascript.SaleOrderController.create,
        controllers.rest.routes.javascript.SaleOrderController.paymentState,
        controllers.rest.routes.javascript.SaleOrderController.wepayInfo,
        controllers.rest.routes.javascript.SaleOrderController.alipayInfo,
        controllers.rest.routes.javascript.SaleOrderController.cashInfo,

        controllers.rest.routes.javascript.AddressController.province,
        controllers.rest.routes.javascript.AddressController.city,
        controllers.rest.routes.javascript.AddressController.district,
        controllers.rest.routes.javascript.AddressController.update,
        controllers.rest.routes.javascript.AddressController.list,
        controllers.rest.routes.javascript.AddressController.delete,
        controllers.rest.routes.javascript.AddressController.get
      )
    ).as("text/javascript")
  }

}
