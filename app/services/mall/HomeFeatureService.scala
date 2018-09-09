package services.mall

import java.text.SimpleDateFormat

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.mall.{HomeFeature, HomeFeatureFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class HomeFeatureService extends CrudService[HomeFeature] {

  override def processFilter(searchBase: Relation1[HomeFeature, HomeFeature],
                             filter: ModelFilter[HomeFeature]): Relation1[HomeFeature, HomeFeature] = {
    val HomeFeatureFilter(idOpt, presentingTypeOpt, titleOpt, sequenceOpt, startAtOpt, closeAtOpt) = filter

    val afterBaseSearch = searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.title like titleOpt.map(value => s"%$value%").?)
          and (record.presentingType like presentingTypeOpt.map(value => s"%$value%").?))

    val pattern = """([<>]?)\W*(\d*)""".r
    val afterSequence = sequenceOpt match {
      case Some(pattern("<", d)) => afterBaseSearch.where(_.sequence.~ <= d.trim.toInt)
      case Some(pattern(">", d)) => afterBaseSearch.where(_.sequence.~ >= d.trim.toInt)
      case Some(pattern("", d))  => afterBaseSearch.where(_.sequence === d.trim.toInt)
      case _                     => afterBaseSearch
    }

    val format      = new SimpleDateFormat("yyyy-MM-dd")
    val datePattern = """([<>]?)\W*([0-9]{4}-[0-9]{2}-[0-9]{2})""".r
    val afterStartAt = startAtOpt match {
      case Some(datePattern("<", d)) => afterSequence.where(_.startAt.~ <= format.parse(d.trim))
      case Some(datePattern(">", d)) => afterSequence.where(_.startAt.~ >= format.parse(d.trim))
      case Some(datePattern("", d))  => afterSequence.where(_.startAt === format.parse(d.trim))
      case _                         => afterSequence
    }

    closeAtOpt match {
      case Some(datePattern("<", d)) => afterStartAt.where(_.closeAt.~ <= format.parse(d.trim))
      case Some(datePattern(">", d)) => afterStartAt.where(_.closeAt.~ >= format.parse(d.trim))
      case Some(datePattern("", d))  => afterStartAt.where(_.closeAt === format.parse(d.trim))
      case _                         => afterStartAt
    }

  }

}
