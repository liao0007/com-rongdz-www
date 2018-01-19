package controllers

import daos.default.mall.SaleOrder.{SaleOrderChannel, SaleOrderDeliveryType}
import play.api.libs.json._
import play.api.mvc.{Result, Results}

import scala.concurrent.Future

/**
  * Created by liangliao on 24/11/16.
  */
package object rest {

  case class SaleOrderCreateRequest(orderNumber: String,
                                    shipToAddressId: Long,
                                    deliveryType: String = SaleOrderDeliveryType.Direct,
                                    channel: String = SaleOrderChannel.OnlineStore,
                                    memo: Option[String] = None)
  object SaleOrderCreateRequest {
    implicit val format: OFormat[SaleOrderCreateRequest] = Json.format[SaleOrderCreateRequest]
  }

  // generic
  case class Good(status: String, details: Option[JsValue] = None)
  object Good {
    def apply(status: String, simpleDetails: String): Good = Good(status, Some(JsString(simpleDetails)))
    def apply(status: String, details: JsValue): Good      = Good(status, Some(details))
    val empty: Good                                        = Good("Ok", None)
    implicit val format: OFormat[Good]                    = Json.format[Good]
  }

  case class Bad(status: String, details: Option[JsValue] = None)
  object Bad {
    def apply(status: String, simpleDetails: String): Bad = Bad(status, Some(JsString(simpleDetails)))
    def apply(status: String, details: JsValue): Bad      = Bad(status, Some(details))
    val empty: Bad                                        = Bad("error", None)
    val invalidJson: Bad                                  = Bad("invalid.json")
    implicit val format: OFormat[Bad]                    = Json.format[Bad]
  }

  trait ResponseSupport { self: Results =>

    /**
      * @param err error to encode info from
      * @param badFormat format of response json
      */
    def badRequestWithMessage(err: JsError)(implicit badFormat: Format[Bad]): Future[Result] =
      Future.successful(BadRequest(Json.toJson(Bad("invalid.json", JsError.toJson(err)))))
  }

}
