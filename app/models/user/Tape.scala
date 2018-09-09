package models.user

import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import models.ActiveRecord
import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 4/13/16.
  */
case class Tape(
                 var userId: Long,
                 var key: String,
                 var value: String
               ) extends ActiveRecord {
  lazy val user: ActiveRecord.BelongsToAssociation[Tape.this.type, User] = belongsTo[User]
}

object Tape extends ActiveRecordCompanion[Tape] with PlayFormSupport[Tape] {

  sealed class Key(val name: String, val um: String, val initial: String, val min: String, val max: String, val step: String)

  object TapeKey {

    case object TZ extends Key("体重", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object SG extends Key("身高", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object LWCY extends Key("领围(衬衣)", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object XW extends Key("胸围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object ZYW extends Key("中腰围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object KYW extends Key("裤腰围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object TW extends Key("臀围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object TGW extends Key("腿根围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object TD extends Key("通档", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object SBW extends Key("上臂围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object SWW extends Key("手腕围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object ZJK extends Key("总肩宽", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object YXC extends Key("右袖长", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object ZXC extends Key("左袖长", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object QBK extends Key("前臂宽", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object HYJC extends Key("后腰节长", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object HYC extends Key("后衣长", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object HYG extends Key("后腰高", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object QYJC extends Key("前腰节长", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object QYG extends Key("前腰高", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object YKC extends Key("右裤长", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object ZKC extends Key("左裤长", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object YXTW extends Key("右小腿围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object ZXTW extends Key("左小腿围", "厘米", "1.8", "1.5", "2.5", "0.25")

    case object YX extends Key("腰形", "/", "2", "0", "0", "0")

    val keys = Seq(TZ, SG, LWCY, XW, ZYW, KYW, TW, TGW, TD, SBW, SWW, ZJK, YXC, ZXC, QBK, HYJC, HYC, HYG, QYJC, QYG, YKC, ZKC, YXTW, ZXTW, YX)

    implicit def fromString(x: String): Option[Key] = keys.find(_.toString == x)

    implicit def toString(key: Key): String = key.toString
  }

  implicit val format: OFormat[Tape] = Json.format[Tape]
}
