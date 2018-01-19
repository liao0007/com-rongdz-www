package daos

import com.github.aselab.activerecord.Timestamps
import com.github.aselab.activerecord.inner.{CRUDable, Optimistic}

/**
  * Created by liangliao on 17/7/16.
  */
abstract class ActiveRecord
  extends com.github.aselab.activerecord.ActiveRecord
    with Optimistic
    with Timestamps
    with Userstamps
    with Statuses

trait IterableAttribute[T] extends Seq[T] {
  protected def all: Seq[T]
  override def length: Int = all.length
  override def apply(idx: Int): T = all(idx)
  override def iterator: Iterator[T] = all.toIterator
}

trait Statuses extends CRUDable {
  var active: Boolean = true
  var systemCreated: Boolean = false

  override def beforeDelete(): Unit = if (systemCreated) throw new Exception("无法删除系统创建数据")
}

trait Userstamps extends CRUDable {
  var createdBy: Option[String] = None
  var updatedBy: Option[String] = None
}