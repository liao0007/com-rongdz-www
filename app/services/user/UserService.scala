package services.user

import java.text.SimpleDateFormat

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.user.User
import models._
import models.default.user.UserFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class UserService extends CrudService[User] {

  override def processFilter(searchBase: Relation1[User, User], filter: ModelFilter[User]): Relation1[User, User] = {
    val UserFilter(idOpt, mobileOpt, nameOpt, genderOpt, birthdayOpt, emailOpt, stateOpt) = filter

    val baseSearch = searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.mobile like mobileOpt.map(value => s"%$value%").?)
          and (record.gender like genderOpt.map(value => s"%$value%").?)
          and (record.email like emailOpt.map(value => s"%$value%").?)
          and record.state === stateOpt.?
    )

    val format      = new SimpleDateFormat("yyyy-MM-dd")
    val datePattern = """([<>]?)\W*([0-9]{4}-[0-9]{2}-[0-9]{2})""".r
    birthdayOpt match {
      case Some(datePattern("<", d)) => baseSearch.where(_.birthday.~ <= format.parse(d.trim))
      case Some(datePattern(">", d)) => baseSearch.where(_.birthday.~ >= format.parse(d.trim))
      case Some(datePattern("", d))  => baseSearch.where(_.birthday === format.parse(d.trim))
      case _                         => baseSearch
    }

  }
}
