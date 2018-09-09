package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.product.{Sku, SkuFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class SkuService extends CrudService[Sku] {

  override def update(record: Sku): record.type = {
    Sku.transaction {
      record.sales foreach { sale =>
        sale.productId = record.productId
        sale.categoryId = record.product.subcategory.categoryId
        sale.subcategoryId = record.product.subcategoryId
        sale.brandId = record.product.brandId
        sale.update
      }
    }
    record.update
  }

  override def processFilter(searchBase: Relation1[Sku, Sku], filter: ModelFilter[Sku]): Relation1[Sku, Sku] = {
    val SkuFilter(idOpt, productIdOpt, skuOpt, unitPriceOpt) = filter

    val afterSku = searchBase.where(
      record =>
        record.id === idOpt.?
          and record.productId === productIdOpt.?
          and (record.sku like skuOpt.map(value => s"%$value%").?)
    )

    val pattern = """([<>]?)\W*(\d*\.?\d*)""".r
    unitPriceOpt match {
      case Some(pattern("<", d)) => afterSku.where(_.unitPrice.~ <= d.trim.toFloat)
      case Some(pattern(">", d)) => afterSku.where(_.unitPrice.~ >= d.trim.toFloat)
      case Some(pattern("", d))  => afterSku.where(_.unitPrice === d.trim.toFloat)
      case _                     => afterSku
    }
  }
}
