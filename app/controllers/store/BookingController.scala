package controllers.store

import java.text.SimpleDateFormat
import javax.inject.Inject

import auth.JWTEnv
import auth.filters.CookieSettings
import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.Silhouette
import daos.default.mall.Booking.BookingState
import daos.default.mall.Sale
import models.mall.SaleFilter
import models.{ModelPager, Paging, ModelSorter}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.Future

class BookingController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[JWTEnv], cookieSettings: CookieSettings)
    extends Controller
    with I18nSupport
    with Paging {

  def index(saleNumberOpt: Option[String]): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Ok(views.html.store.booking.index(routes.BookingController.update(saleNumberOpt), Booking.form))
    }
  }

  def update(saleNumberOpt: Option[String]): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful {
      Booking.form.bindFromRequest.fold(
        { errorForm =>
          Ok(views.html.store.booking.index(routes.BookingController.update(saleNumberOpt), errorForm))
        }, { booking =>
          Booking.transaction {
            Booking.findBy("bookingNumber" -> booking.bookingNumber) match {
              case Some(booked) => Ok(views.html.store.booking.succeed(booked))
              case _            =>
                booking.userId = request.identity.map(_.id)
                booking.state = BookingState.Open
                booking.saleId = Sale.where(_.saleNumber === saleNumberOpt.?).select(_.id).headOption
                Ok(views.html.store.booking.succeed(booking.create))
            }
          }
        }
      )
    }
  }

}
