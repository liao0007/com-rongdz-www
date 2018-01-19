package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.product.AttributeOption
import models._
import models.default.product.AttributeOptionFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class AttributeOptionService extends CrudService[AttributeOption] {

  override def processFilter(searchBase: Relation1[AttributeOption, AttributeOption],
                             filter: ModelFilter[AttributeOption]): Relation1[AttributeOption, AttributeOption] = {
    val AttributeOptionFilter(idOpt, attributeIdOpt, valueOpt, nameOpt, descriptionOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.attributeId === attributeIdOpt.?
          and (record.value like valueOpt.map(value => s"%$value%").?)
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.description like descriptionOpt.map(value => s"%$value%").?)
    )
  }
}
