package services.mall

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.mall.{SaleOrderDetail, SaleOrderDetailAttributeValue, SaleOrderDetailFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class SaleOrderDetailService extends CrudService[SaleOrderDetail] {

  override def create(record: SaleOrderDetail): record.type = {
    SaleOrderDetail.transaction {
      val createdRecord = record.create
      for {
        attributeValue <- createdRecord.sale.sku.attributeValueSet.attributeValues
      } yield {
        SaleOrderDetailAttributeValue(saleOrderDetailId = createdRecord.id, attributeId = attributeValue.attributeId, value = attributeValue.value).create
      }
      createdRecord
    }
  }

  override def update(record: SaleOrderDetail): record.type = {
    SaleOrderDetail.transaction {
      record.saleOrderDetailAttributeValues foreach (_.delete)

      for {
        attributeValue <- record.sale.sku.attributeValueSet.attributeValues
      } yield {
        SaleOrderDetailAttributeValue(saleOrderDetailId = record.id, attributeId = attributeValue.attributeId, value = attributeValue.value).create
      }
      record.update
    }
  }

  override def processFilter(searchBase: Relation1[SaleOrderDetail, SaleOrderDetail],
                             filter: ModelFilter[SaleOrderDetail]): Relation1[SaleOrderDetail, SaleOrderDetail] = {
    val SaleOrderDetailFilter(idOpt, saleOrderIdOpt, saleIdOpt, quantityOpt) = filter

    val afterBaseSearch = searchBase.where(
      record =>
        record.id === idOpt.?
          and record.saleOrderId === saleOrderIdOpt.?
          and record.saleId === saleIdOpt.?
    )

    val pattern = """([<>]?)\W*(\d*\.?\d*)""".r
    quantityOpt match {
      case Some(pattern("<", d)) => afterBaseSearch.where(_.quantity.~ <= d.trim.toFloat)
      case Some(pattern(">", d)) => afterBaseSearch.where(_.quantity.~ >= d.trim.toFloat)
      case Some(pattern("", d))  => afterBaseSearch.where(_.quantity === d.trim.toFloat)
      case _                     => afterBaseSearch
    }

  }

}
