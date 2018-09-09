package services.mall

import java.lang.Long

import com.alipay.api.request.{AlipayTradePrecreateRequest, AlipayTradeWapPayRequest}
import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import com.google.inject.Inject
import controllers.rest.SaleOrderCreateRequest
import models.ModelFilter
import models.mall.{SaleOrder, SaleOrderDetail, SaleOrderDetailAttributeValue, SaleOrderFilter}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import services.{AlipayService, CrudService, WxMpService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *  Created by liangliao on 7/28/16.
  */
class SaleOrderService @Inject()(val cartService: CartService, wxMpService: WxMpService, alipayService: AlipayService)
    extends CrudService[SaleOrder] {

  override def create(record: SaleOrder): record.type = {
    SaleOrder.transaction {
      record.orderNumber = SaleOrder.newOrderNumber(record.userId)
      record.create
    }
  }

  override def update(record: SaleOrder): record.type = {
    SaleOrder.transaction {
      val shipToAddress = ShipToAddress.find(record.shipToAddressId).get
      record.shipToName = shipToAddress.name
      record.shipToProvince = shipToAddress.district.city.province.name
      record.shipToCity = shipToAddress.district.city.name
      record.shipToDistrict = shipToAddress.district.name
      record.shipToMobile = shipToAddress.mobile
      record.shipToAddress = shipToAddress.address
      record.update
    }
  }

  override def processFilter(searchBase: Relation1[SaleOrder, SaleOrder], filter: ModelFilter[SaleOrder]): Relation1[SaleOrder, SaleOrder] = {
    val SaleOrderFilter(idOpt,
                        orderNumberOpt,
                        shipToNameOpt,
                        shipToMobileOpt,
                        totalAmountOpt,
                        channelOpt,
                        deliveryTypeOpt,
                        stateOpt,
                        paymentStateOpt,
                        shippingStateOpt,
                        memoOpt) = filter

    val baseSearch = searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.orderNumber like orderNumberOpt.map(value => s"%$value%").?)
          and (record.shipToName like shipToNameOpt.map(value => s"%$value%").?)
          and (record.shipToMobile like shipToMobileOpt.map(value => s"%$value%").?)
          and (record.memo like memoOpt.map(value => s"%$value%").?)
          and record.channel === channelOpt.?
          and record.deliveryType === deliveryTypeOpt.?
          and record.state === stateOpt.?
          and record.paymentState === paymentStateOpt.?
          and record.shippingState === shippingStateOpt.?
    )

    val pattern = """([<>]?)\W*(\d*\.?\d*)""".r
    totalAmountOpt match {
      case Some(pattern("<", d)) => baseSearch.where(_.totalAmount.~ <= d.trim.toFloat)
      case Some(pattern(">", d)) => baseSearch.where(_.totalAmount.~ >= d.trim.toFloat)
      case Some(pattern("", d))  => baseSearch.where(_.totalAmount === d.trim.toFloat)
      case _                     => baseSearch
    }

  }

  def create(userId: Long, saleOrderCreateRequest: SaleOrderCreateRequest)(implicit request: RequestHeader): Future[SaleOrder] = {
    SaleOrder.findBy("orderNumber" -> saleOrderCreateRequest.orderNumber) match {
      case Some(order) =>
        Future.successful {
          order
        }
      case _ =>
        val shipToAddress = ShipToAddress.find(saleOrderCreateRequest.shipToAddressId).get

        cartService.list(userId) map { cartItems =>
          SaleOrder.transaction {

            val saleOrder: SaleOrder = SaleOrder(
              orderNumber = saleOrderCreateRequest.orderNumber,
              userId = userId,
              totalAmount = 0,
              deliveryType = saleOrderCreateRequest.deliveryType,
              channel = saleOrderCreateRequest.channel,
              memo = saleOrderCreateRequest.memo,
              shipToAddressId = shipToAddress.id,
              shipToName = shipToAddress.name,
              shipToProvince = shipToAddress.district.city.province.name,
              shipToCity = shipToAddress.district.city.name,
              shipToDistrict = shipToAddress.district.name,
              shipToMobile = shipToAddress.mobile,
              shipToAddress = shipToAddress.address
            ).create

            val totalAmount = (cartItems map { cartItem =>
              SaleOrderDetail(saleOrderId = saleOrder.id, saleId = cartItem.sale.id, quantity = cartItem.quantity).create
              cartItem.quantity * cartItem.sale.originalUnitPrice
            }).sum

            //purge cart
            cartService.delete(userId)

            saleOrder.totalAmount = totalAmount
            saleOrder.update

            for {
              saleOrderDetail <- saleOrder.details
              attributeValue  <- saleOrderDetail.sale.sku.attributeValueSet.attributeValues
            } yield {
              SaleOrderDetailAttributeValue(saleOrderDetailId = saleOrderDetail.id,
                                            attributeId = attributeValue.attributeId,
                                            value = attributeValue.value).create
            }

            saleOrder
          }
        }
    }
  }

  def cancel(userId: Long, orderId: Long)(implicit request: RequestHeader) = {
    SaleOrder.find(orderId) match {
      case Some(order) if order.userId == userId =>
        SaleOrder.transaction {
          order.state = SaleOrderState.Disabled.toString
          order.save()
        }
      case _ =>
    }
  }

  def wepayInfo(orderNumber: String, notifyUrl: String, tradeType: String)(implicit request: RequestHeader): Option[Map[String, String]] =
    for {
      saleOrder: SaleOrder <- SaleOrder.findBy("orderNumber" -> orderNumber)
      if saleOrder.paymentState == SaleOrderPaymentState.Open.toString
    } yield {

      val now            = DateTime.now
      val dateTimeFormat = DateTimeFormat.forPattern("yyyyMMddHHmmss")

      val outTradeNumber = saleOrder.orderNumber + tradeType.head + DateTimeFormat.forPattern("sss").print(now)
      saleOrder.paymentOutTradeNumber = Some(outTradeNumber)
      saleOrder.paymentMethod = SaleOrderPaymentMethod.Wepay
      saleOrder.update

      val orderRequest = new WxPayUnifiedOrderRequest()
      orderRequest.setBody("荣定制-" + saleOrder.orderNumber)
      orderRequest.setOutTradeNo(outTradeNumber)
      orderRequest.setProductId(outTradeNumber)
      orderRequest.setTotalFee(Math.round(saleOrder.totalAmount * 100))

      LoginInfo.findBy("providerId" -> WechatProvider.ID, "userId" -> saleOrder.userId) foreach { loginInfo =>
        orderRequest.setOpenid(loginInfo.providerKey)
      }
      orderRequest.setSpbillCreateIp(request.remoteAddress)
      orderRequest.setTimeStart(dateTimeFormat.print(now))
      orderRequest.setTimeExpire(dateTimeFormat.print(now.plusMinutes(5)))
      orderRequest.setTradeType(tradeType)
      orderRequest.setNotifyURL(notifyUrl)

      val wxMpPayService = wxMpService.getPayService
      wxMpPayService.getPayInfo(orderRequest).asScala.toMap
    }

  def alipayInfo(orderNumber: String, notifyUrl: String, tradeType: String)(implicit request: RequestHeader): Option[String] =
    for {
      saleOrder: SaleOrder <- SaleOrder.findBy("orderNumber" -> orderNumber)
      if saleOrder.paymentState == SaleOrderPaymentState.Open.toString
    } yield {

      val outTradeNumber = saleOrder.orderNumber
      saleOrder.paymentOutTradeNumber = Some(outTradeNumber)
      saleOrder.paymentMethod = SaleOrderPaymentMethod.Alipay
      saleOrder.update

      if (tradeType == "QUICK_WAP_PAY") {
        val bizContent = Map(
          "out_trade_no" -> outTradeNumber,
          "total_amount" -> saleOrder.totalAmount.toString,
          "subject"      -> ("荣定制-" + saleOrder.orderNumber),
          "product_code" -> tradeType
        )

        val alipayTradeWapPayRequest = new AlipayTradeWapPayRequest()
        alipayTradeWapPayRequest.setNotifyUrl(notifyUrl)
        alipayTradeWapPayRequest.setReturnUrl(controllers.store.routes.AccountController.saleOrder(orderNumber).absoluteURL())
        alipayTradeWapPayRequest.setBizContent(Json.toJson(bizContent).toString)
        alipayService.pageExecute(alipayTradeWapPayRequest).getBody
      } else {
        val bizContent = Map(
          "out_trade_no" -> outTradeNumber,
          "total_amount" -> saleOrder.totalAmount.toString,
          "subject"      -> ("荣定制-" + saleOrder.orderNumber)
        )

        val alipayTradePrecreateRequest = new AlipayTradePrecreateRequest()
        alipayTradePrecreateRequest.setBizContent(Json.toJson(bizContent).toString)
        alipayTradePrecreateRequest.setNotifyUrl(notifyUrl)
        alipayService.execute(alipayTradePrecreateRequest).getQrCode
      }
    }

}
