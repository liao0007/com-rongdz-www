package services.user

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.user.Permission
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class ToPermissionService extends CrudService[Permission] {

  override def processFilter(searchBase: Relation1[Permission, Permission],
                             filter: ModelFilter[Permission]): Relation1[Permission, Permission] = {
    val PermissionFilter(idOpt, userIdOpt, permissionOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and record.userId === userIdOpt.?
          and record.permission === permissionOpt.?
    )
  }
}
