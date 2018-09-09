package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.product.{AttributeValue, AttributeValueFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class AttributeValueService extends CrudService[AttributeValue] {

  override def processFilter(searchBase: Relation1[AttributeValue, AttributeValue],
                             filter: ModelFilter[AttributeValue]): Relation1[AttributeValue, AttributeValue] = {
    val AttributeValueFilter(idOpt, attributeValueSetIdOpt, attributeIdOpt, valueOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.attributeValueSetId === attributeValueSetIdOpt.?
          and record.attributeId === attributeIdOpt.?
          and (record.value like valueOpt.map(value => s"%$value%").?))
  }
}
