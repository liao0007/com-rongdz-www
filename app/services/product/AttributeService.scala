package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.product.{Attribute, AttributeFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class AttributeService extends CrudService[Attribute] {

  override def processFilter(searchBase: Relation1[Attribute, Attribute], filter: ModelFilter[Attribute]): Relation1[Attribute, Attribute] = {
    val AttributeFilter(idOpt, nameOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?))
  }
}
