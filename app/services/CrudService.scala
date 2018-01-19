package services

import com.github.aselab.activerecord.ActiveRecord.Relation1
import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.reflections.ReflectionUtil.classToARCompanion
import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import daos.ActiveRecord
import models._
import play.api.data.Form

import scala.annotation.tailrec
import scala.reflect.ClassTag

/**
  * Created by liangliao on 12/1/17.
  */
abstract class CrudService[T <: ActiveRecord](implicit val manifest: ClassTag[T]) extends Paging {

  def processFilter(searchBase: Relation1[T, T], filter: ModelFilter[T]): Relation1[T, T]

  val companion: ActiveRecordCompanion[T] with PlayFormSupport[T] =
    classToARCompanion[T](manifest.runtimeClass).asInstanceOf[ActiveRecordCompanion[T] with PlayFormSupport[T]]

  def find(id: Long): Option[T] = companion.find(id)

  def search(pager: ModelPager, filter: ModelFilter[T], sorter: ModelSorter): ModelResult[T] = {
    search(pager, filter, sorter, None)
  }

  def search(pager: ModelPager, filter: ModelFilter[T], sorter: ModelSorter, addOn: Relation1[T, T] => Relation1[T, T]): ModelResult[T] = {
    search(pager, filter, sorter, Some(addOn))
  }

  def search(pager: ModelPager, filter: ModelFilter[T], sorter: ModelSorter, addOnOpt: Option[Relation1[T, T] => Relation1[T, T]] = None): ModelResult[T] = {
    val filteredSearch: Relation1[T, T] = processFilter(companion.all, filter)
    val sortedSearch: Relation1[T, T]   = processSorter(filteredSearch, sorter)
    val totalItems                      = filteredSearch.count.toInt
    val pagination                      = calculatePagination(pager, totalItems)
    val pagedSearchResult               = sortedSearch.page(pagination.recordOffset, pager.size)
    val addOnResult = addOnOpt match {
      case Some(p) => p(pagedSearchResult)
      case _ => pagedSearchResult
    }
    ModelResult(addOnResult.toSeq, pagination, filter, sorter)
  }

  def create(record: T): record.type = {
    companion.transaction {
      record.update
    }
  }

  def update(record: T): record.type = {
    companion.transaction {
      record.update
    }
  }

  def delete(record: T): Boolean = {
    companion.transaction {
      record.delete()
    }
  }

  @tailrec
  final def prev(id: Long, result: Option[T], extraParams: Seq[(String, Any)] = List.empty): Option[T] = {
    val maxIdOpt: Option[Long] = extraParams match {
      case head :: tail => companion.defaultScope.unsafeFindAllBy(head, tail: _*).maximum(_.id)
      case Nil          => companion.defaultScope.maximum(_.id)
    }
    result match {
      case Some(_)                                      => result
      case _ if maxIdOpt.isEmpty                        => None
      case _ if maxIdOpt.isDefined && id > maxIdOpt.get => None
      case _                                            => prev(id + 1, companion.defaultScope.unsafeFindBy(("id", id + 1), extraParams: _*), extraParams)
    }
  }

  @tailrec
  final def next(id: Long, result: Option[T], extraParams: Seq[(String, Any)] = List.empty): Option[T] = {
    val minIdOpt: Option[Long] = extraParams match {
      case head :: tail => companion.defaultScope.unsafeFindAllBy(head, tail: _*).minimum(_.id)
      case Nil          => companion.defaultScope.minimum(_.id)
    }
    result match {
      case Some(_)                                      => result
      case _ if minIdOpt.isEmpty                        => None
      case _ if minIdOpt.isDefined && id < minIdOpt.get => None
      case _                                            => next(id - 1, companion.defaultScope.unsafeFindBy(("id", id - 1), extraParams: _*), extraParams)
    }
  }

  def form(recordOpt: Option[T] = None): Form[T] = recordOpt.fold(companion.form) { record =>
    companion.form(record)
  }

  protected def processSorter(searchBase: Relation1[T, T], sorter: ModelSorter): Relation1[T, T] = {
    searchBase.orderBy(sorter.attribute, sorter.order)
  }

}
