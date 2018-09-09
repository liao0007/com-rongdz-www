package controllers.store

import javax.inject.Inject
import auth.JWTEnv
import auth.filters.CookieSettings
import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.Silhouette
import daos.default.mall.Booking.BookingState
import daos.default.mall.HomeSlider
import daos.default.user.Tape.TapeKey
import models.user.Tape
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import services.WxMpService
import services.mall.{CartService, SaleOrderService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApplicationController @Inject()(val messagesApi: MessagesApi,
                                      silhouette: Silhouette[JWTEnv],
                                      cookieSettings: CookieSettings,
                                      cartService: CartService,
                                      wxMpService: WxMpService,
                                      orderService: SaleOrderService)
  extends Controller
    with I18nSupport {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("StoreRoutes")(
        controllers.store.routes.javascript.ApplicationController.index,
        controllers.store.routes.javascript.ApplicationController.bag,
        controllers.store.routes.javascript.AccountController.signIn,
        controllers.store.routes.javascript.AccountController.signOut,
        controllers.store.routes.javascript.AccountController.signUp,
        controllers.store.routes.javascript.AccountController.saleOrder
      )
    ).as("text/javascript")
  }

  def index(): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      val features = HomeFeature.all.orderBy("sequence", "DESC").toList.groupBy(_.presentingType)
      val sliders = HomeSlider.orderBy("sequence", "DESC").toList
      Ok(views.html.store.index(features, sliders))
    }
  }

  def bag(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    cartService.list(request.identity.id) map { cartItems =>
      Ok(views.html.store.bag(cartItems))
    }
  }

  def checkout(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    cartService.list(request.identity.id) map { cartItems =>
      if (cartItems.isEmpty) {
        Redirect(routes.ApplicationController.bag())
      } else {
        Ok(views.html.store.checkout(cartItems, request.identity.shipToAddresses.toList))
      }

    }
  }

  def tape(returnUrl: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.tape(request.identity.tapes.map(tape => tape.key -> tape).toMap, returnUrl))
    }
  }

  def updateTape(returnUrl: String): Action[Map[String, Seq[String]]] = silhouette.SecuredAction.async(parse.tolerantFormUrlEncoded) {
    implicit request =>
      Future.successful {
        Tape.transaction {
          for {
            key <- TapeKey.keys
            value: Seq[String] <- request.body.get(key.toString)
            headValue <- value.headOption
          } yield {
            Tape.findBy("userId" -> request.identity.id, "key" -> key.toString) match {
              case Some(tape) =>
                tape.value = headValue
                tape.update
              case None =>
                Tape(request.identity.id, key.toString, headValue).create
            }
          }
        }
        Redirect(returnUrl)
      }
  }

  def contact(): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.contact())
    }
  }

  def cooperation(): Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    Ok(views.html.store.cooperation(Booking.form))
  }

  def submitCooperation(): Action[AnyContent] = silhouette.UserAwareAction { implicit request =>
    Booking.form.bindFromRequest.fold(
      { formWithError =>
        Ok(views.html.store.cooperation(formWithError))
      }, { booking =>
        Booking.transaction {
          Booking.findBy("bookingNumber" -> booking.bookingNumber) match {
            case Some(booked) =>
              Redirect(controllers.pinan.routes.BookingController.succeed(booked.bookingNumber))
            case _ =>
              booking.state = BookingState.Open
              booking.copy(memo = Some("【招募】" + booking.memo.getOrElse(""))).create
              Redirect(controllers.pinan.routes.BookingController.succeed(booking.bookingNumber))
          }
        }
      }
    )
  }

}
