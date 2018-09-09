package controllers.rest

import javax.inject.Inject

import auth.JWTEnv
import com.github.aselab.activerecord.dsl._
import com.mohiva.play.silhouette.api.Silhouette
import daos.default.address._
import daos.default.user.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

class AddressController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[JWTEnv]) extends Controller with I18nSupport {

  def province(html: Boolean) = Action { implicit request =>
    val provinces = Province.all.toList

    if (html) {
      val htmlResult = provinces map { province =>
        s"""
          |<option value="${province.id}">${province.name}</option>
        """.stripMargin
      } mkString ""

      Ok(htmlResult)
    } else {
      Ok(Json.toJson(provinces))
    }
  }

  def city(provinceId: Long, html: Boolean) = Action { implicit request =>
    val cities = Province.find(provinceId) map (_.cities.toList) getOrElse List.empty

    if (html) {
      val htmlResult = cities map { city =>
        s"""
           |<option value="${city.id}">${city.name}</option>
        """.stripMargin
      } mkString ""

      Ok(htmlResult)
    } else {
      Ok(Json.toJson(cities))
    }
  }

  def district(cityId: Long, html: Boolean) = Action { implicit request =>
    val districts = City.find(cityId) map (_.districts.toList) getOrElse List.empty

    if (html) {
      val htmlResult = districts map { district =>
        s"""
           |<option value="${district.id}">${district.name}</option>
        """.stripMargin
      } mkString ""

      Ok(htmlResult)
    } else {
      Ok(Json.toJson(districts))
    }
  }

  def list(html: Boolean): Action[AnyContent] =
    silhouette.SecuredAction.async { implicit request =>
      User.find(request.identity.id) match {
        case Some(user) =>
          val addresses = user.shipToAddresses.orderBy("id", "DESC").toList
          if (html) {
            val htmlResult = addresses map { address =>
              s"""
                  |<label class="btn btn-default ${if (address.isDefault) {
                   "active"
                 }}" data-id="${address.id}">
                  |   <input class="toggle" type="radio" name="shipToAddressId" value=${address.id}> ${address.district.city.province.name} ${address.district.city.name} ${address.district.name} ${address.address} （${address.name} 收）${address.mobile}
                  |</label> <a class="btn edit">编辑</a><a class="btn delete">删除</a>
                """.stripMargin
            } mkString """<br/>"""

            Future.successful {
              Ok(htmlResult)
            }
          } else {
            Future.successful {
              Ok(Json.toJson(addresses))
            }
          }
        case _ =>
          Future.successful {
            NotFound
          }
      }
    }

  def get(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    ShipToAddress.find(id) match {
      case Some(shipToAddress) =>
        Future.successful {
          Ok(Json.toJson(shipToAddress))
        }
      case _ =>
        Future.successful {
          NotFound
        }
    }
  }

  def update(idOpt: Option[Long]): Action[AnyContent] =
    silhouette.SecuredAction.async { implicit request =>
      User.find(request.identity.id) match {
        case Some(user) =>
          (idOpt flatMap { id =>
            ShipToAddress.find(id)
          } map { shipToAddress =>
            shipToAddress.isDefault = false
            ShipToAddress.form(shipToAddress)
          } getOrElse ShipToAddress.form).bindFromRequest.fold(
            errors =>
              Future.successful {
                Ok
            }, { shipToAddress =>
              shipToAddress.user := user
              ShipToAddress.transaction {
                if (shipToAddress.isDefault) {
                  ShipToAddress.where(_.userId === user.id).not(_.id === shipToAddress.id) foreach { otherShipToAddress =>
                    otherShipToAddress.isDefault = false
                    otherShipToAddress.save(throws = true)
                  }
                  shipToAddress.save
                }
              }
              Future.successful {
                Ok(shipToAddress.id.toString)
              }
            }
          )
        case _ =>
          Future.successful {
            NotFound
          }
      }
    }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful {
      ShipToAddress.find(id).map(_.delete())
      Ok
    }
  }

}
