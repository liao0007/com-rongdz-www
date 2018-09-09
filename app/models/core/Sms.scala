package models.core

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Json, OFormat}

case class Sms(
                override val id: Long = 0L,
                var mid: Option[String] = None,
                var callNumber: String,
                var content: String,
                var direction: String,
                var state: Option[String] = None
              ) extends ActiveRecord

object Sms extends ActiveRecordCompanion[Sms] with PlayFormSupport[Sms] {

  sealed abstract class DirectionValue(val name: String) extends EnumAttributeValue

  object Direction extends EnumAttribute[DirectionValue] {

    case object Up extends DirectionValue("Up")

    case object Down extends DirectionValue("Down")

    protected def all: Seq[DirectionValue] = Seq[DirectionValue](Up, Down)
  }

  sealed abstract class StateValue(val name: String) extends EnumAttributeValue

  object State extends EnumAttribute[StateValue] {

    case object Code0 extends StateValue("提交成功") {
      override def toString: String = "0"
    }

    case object Code101 extends StateValue("无此用户") {
      override def toString: String = "101"
    }

    case object Code102 extends StateValue("密码错") {
      override def toString: String = "102"
    }

    case object Code103 extends StateValue("提交过快（提交速度超过流速限制）") {
      override def toString: String = "103"
    }

    case object Code104 extends StateValue("系统忙（因平台侧原因，暂时无法处理提交的短信）") {
      override def toString: String = "104"
    }

    case object Code105 extends StateValue("敏感短信（短信内容包含敏感词）") {
      override def toString: String = "105"
    }

    case object Code106 extends StateValue("消息长度错（>536或<=0）") {
      override def toString: String = "106"
    }

    case object Code107 extends StateValue("包含错误的手机号码") {
      override def toString: String = "107"
    }

    case object Code108 extends StateValue("手机号码个数错（群发>50000或<=0）") {
      override def toString: String = "108"
    }

    case object Code109 extends StateValue("无发送额度（该用户可用短信数已使用完）") {
      override def toString: String = "109"
    }

    case object Code110 extends StateValue("不在发送时间内") {
      override def toString: String = "110"
    }

    case object Code113 extends StateValue("extno格式错（非数字或者长度不对）") {
      override def toString: String = "113"
    }

    case object Code116 extends StateValue("签名不合法或未带签名（用户必须带签名的前提下）") {
      override def toString: String = "116"
    }

    case object Code117 extends StateValue("IP地址认证错,请求调用的IP地址不是系统登记的IP地址") {
      override def toString: String = "117"
    }

    case object Code118 extends StateValue("用户没有相应的发送权限（账号被禁止发送）") {
      override def toString: String = "118"
    }

    case object Code119 extends StateValue("用户已过期") {
      override def toString: String = "119"
    }

    case object Code120 extends StateValue("违反放盗用策略(日发限制)--自定义添加") {
      override def toString: String = "120"
    }

    case object Code121 extends StateValue("必填参数。是否需要状态报告，取值true或false") {
      override def toString: String = "121"
    }

    case object Code122 extends StateValue("5分钟内相同账号提交相同消息内容过多") {
      override def toString: String = "122"
    }

    case object Code123 extends StateValue("发送类型错误") {
      override def toString: String = "0"
    }

    protected def all: Seq[StateValue] =
      Seq[StateValue](Code0,
        Code101,
        Code102,
        Code103,
        Code104,
        Code105,
        Code106,
        Code107,
        Code108,
        Code109,
        Code110,
        Code113,
        Code116,
        Code117,
        Code118,
        Code119,
        Code120,
        Code121,
        Code122,
        Code123)
  }

  implicit val format: OFormat[Sms] = Json.format[Sms]
}
