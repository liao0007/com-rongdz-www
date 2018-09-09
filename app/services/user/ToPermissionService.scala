package services.user

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.user.{ToPermission, ToPermissionFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class ToPermissionService extends CrudService[ToPermission] {

  override def processFilter(searchBase: Relation1[ToPermission, ToPermission],
                             filter: ModelFilter[ToPermission]): Relation1[ToPermission, ToPermission] = {
    val ToPermissionFilter(idOpt, userIdOpt, permissionOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.userId === userIdOpt.?
          and record.permission === permissionOpt.?
    )
  }
}
