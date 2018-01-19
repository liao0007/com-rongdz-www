package services.user

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.user.LoginInfo
import models._
import models.default.user.LoginInfoFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class LoginInfoService extends CrudService[LoginInfo] {

  override def processFilter(searchBase: Relation1[LoginInfo, LoginInfo], filter: ModelFilter[LoginInfo]): Relation1[LoginInfo, LoginInfo] = {
    val LoginInfoFilter(idOpt, userIdOpt, providerIdOpt, providerKeyOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.userId === userIdOpt.?
          and record.providerId === providerIdOpt.?
          and (record.providerKey like providerKeyOpt.map(value => s"%$value%").?)
    )
  }
}
