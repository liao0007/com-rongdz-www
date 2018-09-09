package services.user

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.user.ShipToAddress
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class ShipToAddressService extends CrudService[ShipToAddress] {

  override def create(record: ShipToAddress): record.type = {
    ShipToAddress.transaction {
      val createdRecord = record.create
      if (createdRecord.isDefault) {
        ShipToAddress.where(_.userId === createdRecord.userId).not(_.id === createdRecord.id) foreach { otherShipToAddress =>
          otherShipToAddress.isDefault = false
          otherShipToAddress.save
        }
      }
      createdRecord
    }
  }

  override def update(record: ShipToAddress): record.type = {
    ShipToAddress.transaction {
      if (record.isDefault) {
        ShipToAddress.where(_.userId === record.userId).not(_.id === record.id) foreach { otherShipToAddress =>
          otherShipToAddress.isDefault = false
          otherShipToAddress.save
        }
      }
      record.update
    }
  }

  override def processFilter(searchBase: Relation1[ShipToAddress, ShipToAddress],
                             filter: ModelFilter[ShipToAddress]): Relation1[ShipToAddress, ShipToAddress] = {
    val ShipToAddressFilter(idOpt, userIdOpt, mobileOpt, nameOpt, addressOpt, districtIdOpt, isDefaultOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.userId === userIdOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.mobile like mobileOpt.map(value => s"%$value%").?)
          and (record.address like addressOpt.map(value => s"%$value%").?)
          and record.districtId === districtIdOpt.?
          and record.isDefault === isDefaultOpt.?
    )

  }
}
