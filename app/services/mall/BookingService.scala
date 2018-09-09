package services.mall

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.mall.{Booking, BookingFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class BookingService extends CrudService[Booking] {

  override def processFilter(searchBase: Relation1[Booking, Booking], filter: ModelFilter[Booking]): Relation1[Booking, Booking] = {
    val BookingFilter(idOpt, bookingNumberOpt, nameOpt, mobileOpt, cityOpt, addressOpt, memoOpt, stateOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.bookingNumber like bookingNumberOpt.map(value => s"%$value%").?)
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.mobile like mobileOpt.map(value => s"%$value%").?)
          and (record.city like cityOpt.map(value => s"%$value%").?)
          and (record.address like addressOpt.map(value => s"%$value%").?)
          and (record.memo like memoOpt.map(value => s"%$value%").?)
          and (record.state like stateOpt.map(value => s"%$value%").?))
  }

}
