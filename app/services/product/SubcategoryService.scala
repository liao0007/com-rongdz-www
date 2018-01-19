package services.product

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import daos.default.product.Subcategory
import models._
import models.default.product.SubcategoryFilter
import services.CrudService

/**
  *  Created by liangliao on 7/28/16.
  */
class SubcategoryService extends CrudService[Subcategory] {

  override def processFilter(searchBase: Relation1[Subcategory, Subcategory],
                             filter: ModelFilter[Subcategory]): Relation1[Subcategory, Subcategory] = {
    val SubcategoryFilter(idOpt, categoryIdOpt, nameOpt, descriptionOpt) = filter

    searchBase.where(
      subcategory =>
        subcategory.id === idOpt.?
          and subcategory.categoryId === categoryIdOpt.?
          and (subcategory.name like nameOpt.map(value => s"%$value%").?)
          and (subcategory.description like descriptionOpt.map(value => s"%$value%").?)
    )
  }
}
