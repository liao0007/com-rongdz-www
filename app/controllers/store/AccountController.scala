package controllers.store

import javax.inject.Inject
import auth.JWTEnv
import auth.filters.CookieSettings
import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.Silhouette
import daos.default.mall.SaleOrder.{SaleOrderPaymentState, SaleOrderShippingState, SaleOrderState}
import daos.default.user.User
import models.mall.{SaleOrder, SaleOrderFilter}
import models.{ModelFilter, ModelPager, ModelResult, ModelSorter}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.mall.SaleOrderService

import scala.concurrent.Future

class AccountController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[JWTEnv], cookieSettings: CookieSettings, saleOrderService: SaleOrderService)
    extends Controller
    with I18nSupport {

  def index(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      val recentOrders = SaleOrder.where(_.userId === request.identity.id).orderBy("id", "DESC").includes(_.details).toSeq.take(5)
      Ok(views.html.store.account.index(request.identity.shipToAddresses.includes(_.district).toSeq, recentOrders))
    }
  }

  def signIn(returnUrl: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.account.signIn(returnUrl))
    }
  }

  def signUp(returnUrl: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.account.signUp(returnUrl))
    }
  }

  def signOut(returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      val protocol = if (request.secure) "https://" else "http://"
      Redirect(protocol + request.host + returnUrl)
        .discardingCookies(DiscardingCookie(cookieSettings.name, cookieSettings.path, cookieSettings.domain, cookieSettings.secure))
    }
  }

  def changePassword(returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.account.changePassword(returnUrl))
    }
  }

  def resetPassword(returnUrl: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.account.resetPassword(returnUrl))
    }
  }

  def changeName: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.account.changeName(nameUpdateRequestForm))
    }
  }

  def handleChangeName: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      nameUpdateRequestForm.bindFromRequest.fold({ errorForm =>
        Ok(views.html.store.account.changeName(nameUpdateRequestForm)).flashing("danger" -> "请填写有效昵称")
      }, { nameUpdateRequest =>
        User.transaction {
          val user = request.identity
          user.name = nameUpdateRequest.name
          user.save()
        }
        Redirect(routes.AccountController.index())
      })
    }
  }

//  addresses
  def newAddress(returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.account.address(routes.AccountController.createAddress(returnUrl), ShipToAddress.form, returnUrl))
    }
  }
  def createAddress(returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    ShipToAddress.form.bindFromRequest.fold(
      errors =>
        Future.successful {
          Ok(views.html.store.account.address(routes.AccountController.createAddress(returnUrl), errors, returnUrl))
      }, { shipToAddress =>
        ShipToAddress.transaction {
          if (shipToAddress.isDefault) {
            ShipToAddress.where(_.userId === request.identity.id).not(_.id === shipToAddress.id) foreach { otherShipToAddress =>
              otherShipToAddress.isDefault = false
              otherShipToAddress.save
            }
          }
          shipToAddress.user := request.identity
          shipToAddress.save
        }
        Future.successful {
          Redirect(returnUrl)
        }
      }
    )
  }

  def editAddress(id: Long, returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      ShipToAddress.find(id) match {
        case Some(shipToAddress) =>
          Ok(views.html.store.account.address(routes.AccountController.updateAddress(id, returnUrl), ShipToAddress.form(shipToAddress), returnUrl))
        case _ => NotFound
      }
    }
  }
  def updateAddress(id: Long, returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    ShipToAddress.find(id) match {
      case Some(shipToAddress) =>
        ShipToAddress
          .form(shipToAddress)
          .bindFromRequest
          .fold(
            errors =>
              Future.successful {
                Ok(views.html.store.account.address(routes.AccountController.updateAddress(id), errors, returnUrl))
            }, { shipToAddress =>
              ShipToAddress.transaction {
                if (shipToAddress.isDefault) {
                  ShipToAddress.where(_.userId === request.identity.id).not(_.id === shipToAddress.id) foreach { otherShipToAddress =>
                    otherShipToAddress.isDefault = false
                    otherShipToAddress.save
                  }
                }
                shipToAddress.user := request.identity
                shipToAddress.save
              }
              Future.successful {
                Redirect(returnUrl)
              }
            }
          )
      case None => Future.successful { NotFound }
    }
  }

  def deleteAddress(id: Long, returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      ShipToAddress.find(id) match {
        case Some(shipToAddress) =>
          shipToAddress.delete()
          Redirect(returnUrl)
        case _ => NotFound
      }
    }
  }

  def saleOrders(pager: ModelPager, filter: SaleOrderFilter, sorter: ModelSorter): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      val modelResult: ModelResult[SaleOrder] = saleOrderService.search(pager, filter, sorter, {
        relation => relation.where(_.userId === request.identity.id)
      })
      Ok(views.html.store.account.saleOrders(modelResult.records, modelResult.pagination, filter, sorter))
    }
  }

  def saleOrder(orderNumber: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    SaleOrder.findBy("orderNumber" -> orderNumber) match {
      case Some(order: SaleOrder) => Future.successful(Ok(views.html.store.account.saleOrder(order)))
      case _                      => Future.successful(NotFound)
    }
  }

  def cancelSaleOrder(orderNumber: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    SaleOrder.findBy("orderNumber" -> orderNumber) match {
      case Some(order: SaleOrder)
          if order.state == SaleOrderState.Created.toString && order.paymentState == SaleOrderPaymentState.Open.toString && order.userId == request.identity.id =>
        order.state = SaleOrderState.Disabled
        order.paymentState = SaleOrderPaymentState.Closed
        order.shippingState = SaleOrderShippingState.Disabled
        order.update
        Future.successful(Redirect(routes.AccountController.saleOrder(order.orderNumber)))
      case _ => Future.successful(NotFound)
    }
  }

}
