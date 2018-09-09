package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.product.{AttributeSet, AttributeSetFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class AttributeSetService extends CrudService[AttributeSet] {

  override def processFilter(searchBase: Relation1[AttributeSet, AttributeSet],
                             filter: ModelFilter[AttributeSet]): Relation1[AttributeSet, AttributeSet] = {
    val AttributeSetFilter(idOpt, nameOpt, descriptionOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.description like descriptionOpt.map(value => s"%$value%").?))
  }
}
