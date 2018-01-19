package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.product.Brand
import models._
import models.default.product.BrandFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class BrandService extends CrudService[Brand] {

  override def processFilter(searchBase: Relation1[Brand, Brand], filter: ModelFilter[Brand]): Relation1[Brand, Brand] = {
    val BrandFilter(idOpt, nameOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?))
  }
}
