package models

import com.github.aselab.activerecord.{ActiveRecordTables, PlaySupport}
import models.core._
import models.crm.Page
import models.mall._
import models.product._
import models.user._

/**
  * Created by liangliao on 4/14/16.
  */
object Tables extends ActiveRecordTables with PlaySupport {

  //core
  val image: _root_.com.github.aselab.activerecord.dsl.Table[Image] = table[Image]("core_image")
  val sms: _root_.com.github.aselab.activerecord.dsl.Table[Sms] = table[Sms]("core_sms")
  val wepayLog: _root_.com.github.aselab.activerecord.dsl.Table[WepayLog] = table[WepayLog]("core_wepay_log")
  val alipayLog: _root_.com.github.aselab.activerecord.dsl.Table[AlipayLog] = table[AlipayLog]("core_alipay_log")

  //address
  val province: _root_.com.github.aselab.activerecord.dsl.Table[Province] = table[Province]("address_province")
  val city: _root_.com.github.aselab.activerecord.dsl.Table[City] = table[City]("address_city")
  val district: _root_.com.github.aselab.activerecord.dsl.Table[District] = table[District]("address_district")

  //product
  val brand: _root_.com.github.aselab.activerecord.dsl.Table[Brand] = table[Brand]("product_brand")
  val category: _root_.com.github.aselab.activerecord.dsl.Table[Category] = table[Category]("product_category")
  val subcategory: _root_.com.github.aselab.activerecord.dsl.Table[Subcategory] = table[Subcategory]("product_subcategory")
  val product: _root_.com.github.aselab.activerecord.dsl.Table[Product] = table[Product]("product")
  val sku: _root_.com.github.aselab.activerecord.dsl.Table[Sku] = table[Sku]("product_sku")
  val attribute: _root_.com.github.aselab.activerecord.dsl.Table[Attribute] = table[Attribute]("product_attribute")
  val attributeOption: _root_.com.github.aselab.activerecord.dsl.Table[AttributeOption] = table[AttributeOption]("product_attribute_option")
  val attributeSet: _root_.com.github.aselab.activerecord.dsl.Table[AttributeSet] = table[AttributeSet]("product_attribute_set")
  val attributeSetDetail: _root_.com.github.aselab.activerecord.dsl.Table[AttributeSetDetail] =
    table[AttributeSetDetail]("product_attribute_set_detail")
  val attributeValue: _root_.com.github.aselab.activerecord.dsl.Table[AttributeValue] = table[AttributeValue]("product_attribute_value")
  val attributeValueSet: _root_.com.github.aselab.activerecord.dsl.Table[AttributeValueSet] = table[AttributeValueSet]("product_attribute_value_set")

  //mall
  val homeSection: _root_.com.github.aselab.activerecord.dsl.Table[HomeSection] = table[HomeSection]("mall_home_section")
  val homeSlider: _root_.com.github.aselab.activerecord.dsl.Table[HomeSlider] = table[HomeSlider]("mall_home_slider")
  val homeFeature: _root_.com.github.aselab.activerecord.dsl.Table[HomeFeature] = table[HomeFeature]("mall_home_feature")
  val sale: _root_.com.github.aselab.activerecord.dsl.Table[Sale] = table[Sale]("mall_sale")
  val saleRate: _root_.com.github.aselab.activerecord.dsl.Table[SaleRate] = table[SaleRate]("mall_sale_rate")
  val saleOrder: _root_.com.github.aselab.activerecord.dsl.Table[SaleOrder] = table[SaleOrder]("mall_sale_order")
  val saleOrderDetail: _root_.com.github.aselab.activerecord.dsl.Table[SaleOrderDetail] = table[SaleOrderDetail]("mall_sale_order_detail")
  val saleOrderDetailAttributeValue: _root_.com.github.aselab.activerecord.dsl.Table[SaleOrderDetailAttributeValue] =
    table[SaleOrderDetailAttributeValue]("mall_sale_order_detail_attribute_value")
  val booking: _root_.com.github.aselab.activerecord.dsl.Table[Booking] = table[Booking]("mall_booking")
  val bookingFollowup: _root_.com.github.aselab.activerecord.dsl.Table[BookingFollowup] = table[BookingFollowup]("mall_booking_followup")

  //crm
  val pages: _root_.com.github.aselab.activerecord.dsl.Table[Page] = table[Page]("crm_page")

  //user
  val user: _root_.com.github.aselab.activerecord.dsl.Table[User] = table[User]("user")
  val userLoginInfo: _root_.com.github.aselab.activerecord.dsl.Table[LoginInfo] = table[LoginInfo]("user_login_info")
  val userPasswordInfo: _root_.com.github.aselab.activerecord.dsl.Table[PasswordInfo] = table[PasswordInfo]("user_password_info")
  val userToken: _root_.com.github.aselab.activerecord.dsl.Table[Token] = table[Token]("user_token")
  val userPermission: _root_.com.github.aselab.activerecord.dsl.Table[ToPermission] = table[ToPermission]("user_to_permission")
  val userShipToAddress: _root_.com.github.aselab.activerecord.dsl.Table[ShipToAddress] = table[ShipToAddress]("user_ship_to_address")
  val tape: _root_.com.github.aselab.activerecord.dsl.Table[Tape] = table[Tape]("user_tape")
}
