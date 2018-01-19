package models.default.mall

import daos.default.mall.Sale
import daos.default.product.{Category, Subcategory}
import models.ModelFilter
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

/**
  * Created by liangliao on 21/11/16.
  */
case class SaleFilter(idOpt: Option[Long] = None,
                      productIdOpt: Option[Long] = None,
                      skuIdOpt: Option[Long] = None,
                      brandIdOpt: Option[Long] = None,
                      categoryIdOpt: Option[Long] = None,
                      subcategoryIdOpt: Option[Long] = None,
                      saleNumberOpt: Option[String] = None,
                      titleOpt: Option[String] = None,
                      descriptionOpt: Option[String] = None,
                      unitPriceOpt: Option[String] = None,
                      originalUnitPriceOpt: Option[String] = None,
                      startAtOpt: Option[String] = None,
                      closeAtOpt: Option[String] = None,
                      activeOpt: Option[Boolean] = None,
                      keywordOpt: Option[String] = None,
                      sequenceOpt: Option[String] = None)
    extends ModelFilter[Sale] {
  lazy val category: Option[Category] = categoryIdOpt.flatMap(categoryId => Category.find(categoryId))
  lazy val subcategory: Option[Subcategory] =
    subcategoryIdOpt.flatMap(subcategoryId => Subcategory.find(subcategoryId))
}
object SaleFilter {

  implicit def queryStringBinder(implicit longBinder: QueryStringBindable[Long],
                                 intBinder: QueryStringBindable[Int],
                                 floatBinder: QueryStringBindable[Float],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 stringBinder: QueryStringBindable[String]) =
    new QueryStringBindable[SaleFilter] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SaleFilter]] = {
        val idOpt = longBinder.bind(key + ".id", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val productIdOpt = longBinder.bind(key + ".pid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }
        val skuIdOpt = longBinder.bind(key + ".sid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }

        val brandIdOpt = longBinder.bind(key + ".bid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }
        val categoryIdOpt = longBinder.bind(key + ".cid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }
        val subcategoryIdOpt = longBinder.bind(key + ".scid", params).flatMap {
          case Right(value) => Some(value)
          case _            => None
        }
        val unitPriceOpt = stringBinder.bind(key + ".price", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val titleOpt = stringBinder.bind(key + ".title", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val descriptionOpt = stringBinder.bind(key + ".desc", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val saleNumberOpt = stringBinder.bind(key + ".snum", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val originalUnitPriceOpt = stringBinder.bind(key + ".oprice", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val startAtOpt = stringBinder.bind(key + ".start", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }
        val closeAtOpt = stringBinder.bind(key + ".close", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val keywordOpt = stringBinder.bind(key + ".keyword", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val sequenceOpt = stringBinder.bind(key + ".seq", params).flatMap {
          case Right(value) if value.length > 0 => Some(value)
          case _                                => None
        }

        val activeOpt = booleanBinder.bind(key + ".active", params).flatMap {
          case Right(value) => Some(value)
          case _                                => None
        }

        Some(
          Right(
            SaleFilter(idOpt,
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
                       sequenceOpt)))
      }

      override def unbind(key: String, filter: SaleFilter): String = {
        Seq(filter.idOpt map (value => longBinder.unbind(key + ".id", value)),
            filter.productIdOpt map (value => longBinder.unbind(key + ".pid", value)),
            filter.skuIdOpt map (value => longBinder.unbind(key + ".sid", value)),
            filter.brandIdOpt map (value => longBinder.unbind(key + ".bid", value)),
            filter.categoryIdOpt map (value => longBinder.unbind(key + ".cid", value)),
            filter.saleNumberOpt map (value => stringBinder.unbind(key + ".snum", value)),
            filter.titleOpt map (value => stringBinder.unbind(key + ".title", value)),
            filter.descriptionOpt map (value => stringBinder.unbind(key + ".desc", value)),
            filter.subcategoryIdOpt map (value => longBinder.unbind(key + ".scid", value)),
            filter.unitPriceOpt map (value => stringBinder.unbind(key + ".price", value)),
            filter.originalUnitPriceOpt map (value => stringBinder.unbind(key + ".oprice", value)),
            filter.startAtOpt map (value => stringBinder.unbind(key + ".start", value)),
            filter.closeAtOpt map (value => stringBinder.unbind(key + ".close", value)),
            filter.activeOpt map (value => booleanBinder.unbind(key + ".active", value)),
            filter.keywordOpt map (value => stringBinder.unbind(key + ".keyword", value)),
            filter.sequenceOpt map (value => stringBinder.unbind(key + ".seq", value))).flatten.mkString("&")
      }
    }

  implicit val format: OFormat[ModelFilter[Sale]] = Json.format[SaleFilter].asInstanceOf[OFormat[ModelFilter[Sale]]]
}
