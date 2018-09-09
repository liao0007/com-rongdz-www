package services.mall

import java.text.SimpleDateFormat

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.mall.{SaleRate, SaleRateFilter}
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class SaleRateService extends CrudService[SaleRate] {

  override def processFilter(searchBase: Relation1[SaleRate, SaleRate], filter: ModelFilter[SaleRate]): Relation1[SaleRate, SaleRate] = {
    val SaleRateFilter(idOpt, codeOpt, descriptionOpt, imageOpt, rateTypeOpt, rateOpt, startAtOpt, closeAtOpt) = filter

    val afterBaseSearch = searchBase.where(
      record =>
        record.id === idOpt.?
          and record.code === codeOpt.?
          and record.rateType === rateTypeOpt.?
          and (record.description like descriptionOpt.map(value => s"%$value%").?)
          and (record.image like imageOpt.map(value => s"%$value%").?)
    )

    val pattern = """([<>]?)\W*(\d*\.?\d*)""".r
    val afterRateOpt = rateOpt match {
      case Some(pattern("<", d)) => afterBaseSearch.where(_.rate.~ <= d.trim.toFloat)
      case Some(pattern(">", d)) => afterBaseSearch.where(_.rate.~ >= d.trim.toFloat)
      case Some(pattern("", d))  => afterBaseSearch.where(_.rate === d.trim.toFloat)
      case _                     => afterBaseSearch
    }

    val format      = new SimpleDateFormat("yyyy-MM-dd")
    val datePattern = """([<>]?)\W*([0-9]{4}-[0-9]{2}-[0-9]{2})""".r
    val afterStartAt = startAtOpt match {
      case Some(datePattern("<", d)) => afterRateOpt.where(_.startAt.~ <= format.parse(d.trim))
      case Some(datePattern(">", d)) => afterRateOpt.where(_.startAt.~ >= format.parse(d.trim))
      case Some(datePattern("", d))  => afterRateOpt.where(_.startAt === format.parse(d.trim))
      case _                         => afterRateOpt
    }

    closeAtOpt match {
      case Some(datePattern("<", d)) => afterStartAt.where(_.closeAt.~ <= format.parse(d.trim))
      case Some(datePattern(">", d)) => afterStartAt.where(_.closeAt.~ >= format.parse(d.trim))
      case Some(datePattern("", d))  => afterStartAt.where(_.closeAt === format.parse(d.trim))
      case _                         => afterStartAt
    }

  }

}
