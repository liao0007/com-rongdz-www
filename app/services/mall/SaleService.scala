package services.mall

import java.text.SimpleDateFormat

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import models._
import models.mall.Sale
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class SaleService extends CrudService[Sale] {

  override def create(record: Sale): record.type = {
    Sale.transaction {
      record.productId = record.sku.productId
      record.title = record.sku.title
      record.description = record.sku.description
      record.categoryId = record.sku.product.subcategory.categoryId
      record.subcategoryId = record.sku.product.subcategoryId
      record.brandId = record.sku.product.brandId
      record.saleNumber = Sale.newSaleNumber(record.sku.sku)
      record.create
    }
  }

  override def update(record: Sale): record.type = {
    Sale.transaction {
      record.productId = record.sku.productId
      record.categoryId = record.sku.product.subcategory.categoryId
      record.subcategoryId = record.sku.product.subcategoryId
      record.brandId = record.sku.product.brandId
      record.saleNumber = Sale.newSaleNumber(record.sku.sku)
      record.update
    }
  }

  override def processFilter(searchBase: Relation1[Sale, Sale], filter: ModelFilter[Sale]): Relation1[Sale, Sale] = {
    val SaleFilter(idOpt,
                   productIdOpt,
                   skuIdOpt,
                   brandIdOpt,
                   categoryIdOpt,
                   subcategoryIdOpt,
                   saleNumberOpt,
                   titleOpt,
                   descriptionOpt,
                   unitPriceOpt,
                   originalUnitPriceOpt,
                   startAtOpt,
                   closeAtOpt,
                   activeOpt,
                   keywordOpt,
                   sequenceOpt) = filter

    val afterBaseSearch = searchBase.where(
      record =>
        record.id === idOpt.?
          and record.productId === productIdOpt.?
          and record.skuId === skuIdOpt.?
          and record.brandId === brandIdOpt.?
          and record.categoryId === categoryIdOpt.?
          and record.subcategoryId === subcategoryIdOpt.?
          and record.active === activeOpt.?
          and (record.saleNumber like saleNumberOpt.map(value => s"%$value%").?)
          and (record.title like titleOpt.map(value => s"%$value%").?)
          and (record.description like descriptionOpt.map(value => s"%$value%").?)
          and (record.keyword like keywordOpt.map(value => s"%$value%").?)
    )

    val pattern = """([<>]?)\W*(\d*\.?\d*)""".r
    val afterUnitPriceOpt = unitPriceOpt match {
      case Some(pattern("<", d)) => afterBaseSearch.where(_.unitPrice.~ <= d.trim.toFloat)
      case Some(pattern(">", d)) => afterBaseSearch.where(_.unitPrice.~ >= d.trim.toFloat)
      case Some(pattern("", d))  => afterBaseSearch.where(_.unitPrice === d.trim.toFloat)
      case _                     => afterBaseSearch
    }

    val afterOriginalUnitPriceOpt = originalUnitPriceOpt match {
      case Some(pattern("<", d)) => afterUnitPriceOpt.where(_.originalUnitPrice.~ <= d.trim.toFloat)
      case Some(pattern(">", d)) => afterUnitPriceOpt.where(_.originalUnitPrice.~ >= d.trim.toFloat)
      case Some(pattern("", d))  => afterUnitPriceOpt.where(_.originalUnitPrice === d.trim.toFloat)
      case _                     => afterUnitPriceOpt
    }

    val afterSequenceOpt = sequenceOpt match {
      case Some(pattern("<", d)) => afterOriginalUnitPriceOpt.where(_.sequence.~ <= d.trim.toFloat)
      case Some(pattern(">", d)) => afterOriginalUnitPriceOpt.where(_.sequence.~ >= d.trim.toFloat)
      case Some(pattern("", d))  => afterOriginalUnitPriceOpt.where(_.sequence === d.trim.toFloat)
      case _                     => afterOriginalUnitPriceOpt
    }

    val format      = new SimpleDateFormat("yyyy-MM-dd")
    val datePattern = """([<>]?)\W*([0-9]{4}-[0-9]{2}-[0-9]{2})""".r
    val afterStartAt = startAtOpt match {
      case Some(datePattern("<", d)) => afterSequenceOpt.where(_.startAt.~ <= format.parse(d.trim))
      case Some(datePattern(">", d)) => afterSequenceOpt.where(_.startAt.~ >= format.parse(d.trim))
      case Some(datePattern("", d))  => afterSequenceOpt.where(_.startAt === format.parse(d.trim))
      case _                         => afterSequenceOpt
    }

    closeAtOpt match {
      case Some(datePattern("<", d)) => afterStartAt.where(_.closeAt.~ <= format.parse(d.trim))
      case Some(datePattern(">", d)) => afterStartAt.where(_.closeAt.~ >= format.parse(d.trim))
      case Some(datePattern("", d))  => afterStartAt.where(_.closeAt === format.parse(d.trim))
      case _                         => afterStartAt
    }

  }

}
