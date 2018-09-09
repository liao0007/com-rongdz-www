package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.mall.Sale
import models.product.{Product, ProductFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class ProductService extends CrudService[Product] {

  override def update(record: Product): record.type = {
    Product.transaction {
      record.sales foreach { sale =>
        sale.categoryId = record.subcategory.categoryId
        sale.subcategoryId = record.subcategoryId
        sale.brandId = record.brandId
        sale.update
      }
    }
    record.update
  }

  override def processFilter(searchBase: Relation1[Product, Product], filter: ModelFilter[Product]): Relation1[Product, Product] = {
    val ProductFilter(idOpt, brandIdOpt, subcategoryIdOpt, mkuOpt, nameOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.brandId === brandIdOpt.?
          and record.subcategoryId === subcategoryIdOpt.?
          and (record.mku like mkuOpt.map(value => s"%$value%").?)
          and (record.name like nameOpt.map(value => s"%$value%").?)
    )
  }

}
