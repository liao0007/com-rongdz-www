package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.product.{AttributeValue, AttributeValueSet}
import models._
import models.default.product.AttributeValueSetFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class AttributeValueSetService extends CrudService[AttributeValueSet] {

  override def create(record: AttributeValueSet): record.type = {
    AttributeValueSet.transaction {
      val createdRecord = record.create
      createdRecord.attributeSet.toOption map { attributeSet =>
        attributeSet.attributeSetDetails.toList map { attributeSetDetail =>
          AttributeValue(attributeValueSetId = createdRecord.id, attributeId = attributeSetDetail.attributeId).create
        }
      }
      createdRecord
    }
  }

  override def update(record: AttributeValueSet): record.type = {
    AttributeValueSet.transaction {
      record.attributeValues foreach (_.delete)

      record.attributeSet.toOption map { attributeSet =>
        attributeSet.attributeSetDetails.toList map { attributeSetDetail =>
          AttributeValue(attributeValueSetId = record.id, attributeId = attributeSetDetail.attributeId).create
        }
      }
      record.update
    }
  }

  override def processFilter(searchBase: Relation1[AttributeValueSet, AttributeValueSet],
                             filter: ModelFilter[AttributeValueSet]): Relation1[AttributeValueSet, AttributeValueSet] = {
    val AttributeValueSetFilter(idOpt, attributeSetIdOpt, nameOpt, descriptionOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.attributeSetId === attributeSetIdOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.description like descriptionOpt.map(value => s"%$value%").?))
  }
}
