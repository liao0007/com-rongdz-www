package services.crm

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.crm.Page
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class PageService extends CrudService[Page] {

  override def processFilter(searchBase: Relation1[Page, Page], filter: ModelFilter[Page]): Relation1[Page, Page] = {
    val PageFilter(idOpt, nameOpt, contentOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.content like contentOpt.map(value => s"%$value%").?)
    )
  }

}
