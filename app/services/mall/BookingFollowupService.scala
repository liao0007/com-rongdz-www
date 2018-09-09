package services.mall

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.mall.BookingFollowup
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class BookingFollowupService extends CrudService[BookingFollowup] {

  override def processFilter(searchBase: Relation1[BookingFollowup, BookingFollowup],
                             filter: ModelFilter[BookingFollowup]): Relation1[BookingFollowup, BookingFollowup] = {
    val BookingFollowupFilter(idOpt, bookingIdOpt, descriptionOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.bookingId === bookingIdOpt.?
          and (record.description like descriptionOpt.map(value => s"%$value%").?)
    )
  }

}
