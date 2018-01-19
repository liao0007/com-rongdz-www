package services.mall

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.mall.SaleOrderDetailAttributeValue
import models._
import models.default.mall.SaleOrderDetailAttributeValueFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class SaleOrderDetailAttributeValueService extends CrudService[SaleOrderDetailAttributeValue] {

  override def processFilter(
      searchBase: Relation1[SaleOrderDetailAttributeValue, SaleOrderDetailAttributeValue],
      filter: ModelFilter[SaleOrderDetailAttributeValue]): Relation1[SaleOrderDetailAttributeValue, SaleOrderDetailAttributeValue] = {
    val SaleOrderDetailAttributeValueFilter(idOpt, saleOrderDetailIdOpt, attributeIdOpt, valueOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.saleOrderDetailId === saleOrderDetailIdOpt.?
          and record.attributeId === attributeIdOpt.?
          and (record.value like valueOpt.map(value => s"%$value%").?)
    )

  }

}
