package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.product.{AttributeSetDetail, AttributeValue}
import models._
import models.default.product.AttributeSetDetailFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class AttributeSetDetailService extends CrudService[AttributeSetDetail] {

  override def create(record: AttributeSetDetail): record.type = {
    AttributeSetDetail.transaction {
      record.attributeSet.attributeValueSets foreach { attributeValueSet =>
        AttributeValue(attributeId = record.attributeId, attributeValueSetId = attributeValueSet.id).create
      }
      record.update
    }
  }

  override def delete(record: AttributeSetDetail): Boolean = {
    AttributeSetDetail.transaction {
      record.attributeSet.attributeValueSets foreach { attributeValueSet =>
        AttributeValue.findBy("attributeId" -> record.attributeId, "attributeValueSetId" -> attributeValueSet.id).map(_.delete())
      }
      record.delete()
    }
  }

  override def processFilter(searchBase: Relation1[AttributeSetDetail, AttributeSetDetail],
                             filter: ModelFilter[AttributeSetDetail]): Relation1[AttributeSetDetail, AttributeSetDetail] = {
    val AttributeSetDetailFilter(idOpt, attributeSetIdOpt, attributeIdOpt, sequenceOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.attributeSetId === attributeSetIdOpt.?
          and record.attributeId === attributeIdOpt.?
          and record.sequence === sequenceOpt.?
    )
  }
}
