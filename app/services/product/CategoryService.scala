package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.product.Category
import models._
import models.default.product.CategoryFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class CategoryService extends CrudService[Category] {

  override def processFilter(searchBase: Relation1[Category, Category], filter: ModelFilter[Category]): Relation1[Category, Category] = {
    val CategoryFilter(idOpt, nameOpt, descriptionOpt, isCustomOpt) = filter

    searchBase.where(
      record =>
        record.id === idOpt.?
          and (record.name like nameOpt.map(value => s"%$value%").?)
          and (record.description like descriptionOpt.map(value => s"%$value%").?)
    )
  }
}
