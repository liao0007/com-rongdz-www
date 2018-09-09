package controllers.pinan

import javax.inject.Inject
import daos.default.mall.Booking.BookingState
import models.mall.Booking
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.twirl.api.Html

class BookingController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  private def versions(requestHeader: RequestHeader): (Form[Booking]) => Map[String, Html] = { (form: Form[Booking]) =>
    implicit val request = requestHeader

    Map(
      "v1" -> views.html.pinan.booking.v1(form),
      "v2" -> views.html.pinan.booking.v2(form),
      "v3" -> views.html.pinan.booking.v3(form),
      "v4" -> views.html.pinan.booking.v4(form),
      "v5" -> views.html.pinan.booking.v5(form),
      "v6" -> views.html.pinan.booking.v6(form)
    )
  }

  def index(version: String) = Action { implicit request =>
    val cases: Map[String, Html] = versions(request)(Booking.form)
    Ok(cases(version))
  }

  def book(version: String) = Action { implicit request =>
    Booking.form.bindFromRequest.fold(
      { formWithError =>
        Ok(versions(request)(formWithError)(version))
      }, { booking =>
        Booking.transaction {
          Booking.findBy("bookingNumber" -> booking.bookingNumber) match {
            case Some(booked) =>
              Redirect(routes.BookingController.succeed(booked.bookingNumber))
            case _ =>
              booking.state = BookingState.Open
              booking.create
              Redirect(routes.BookingController.succeed(booking.bookingNumber))
          }
        }
      }
    )
  }

  def succeed(bookingNumber: String) = Action { implicit request =>
    Booking.findBy("bookingNumber" -> bookingNumber) match {
      case Some(booking) => Ok(views.html.pinan.booking.succeed(booking))
      case _             => Redirect(routes.BookingController.index())
    }

  }

}
