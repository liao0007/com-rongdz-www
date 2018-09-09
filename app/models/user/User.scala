package models.user

import java.util.Date

import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import com.mohiva.play.silhouette.api.Identity
import models.mall.SaleOrder
import models.user.User.Gender
import models.{ActiveRecord, EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 4/13/16.
  */
case class User(
                 override val id: Long = 0L,
                 var name: String,
                 var avatar: Option[String] = None,
                 @Format(value = """^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$""", message = "请填写有效手机号码") var mobile: Option[String] = None,
                 var gender: String = Gender.Secret,
                 var birthday: Option[Date] = None,
                 var email: Option[String] = None,
                 var state: String
               ) extends ActiveRecord with Identity {
  lazy val tokens: ActiveRecord.HasManyAssociation[User.this.type, Token] = hasMany[Token]
  lazy val loginInfos: ActiveRecord.HasManyAssociation[User.this.type, LoginInfo] = hasMany[LoginInfo]
  lazy val toPermissions: ActiveRecord.HasManyAssociation[User.this.type, Permission] = hasMany[Permission]
  lazy val shipToAddresses: ActiveRecord.HasManyAssociation[User.this.type, ShipToAddress] =
    hasMany[ShipToAddress]
  lazy val orders: ActiveRecord.HasManyAssociation[User.this.type, SaleOrder] = hasMany[SaleOrder]
  lazy val tapes: ActiveRecord.HasManyAssociation[User.this.type, Tape] = hasMany[Tape]
}

object User extends ActiveRecordCompanion[User] with PlayFormSupport[User] {

  sealed class StateValue(val name: String) extends EnumAttributeValue

  object State extends EnumAttribute[StateValue] {

    case object Created extends StateValue("Created")

    case object Activated extends StateValue("Activated")

    case object Disabled extends StateValue("Disabled")

    protected def all: Seq[StateValue] = Seq[StateValue](Created, Activated, Disabled)

  }

  sealed class GenderValue(val name: String) extends EnumAttributeValue

  object Gender extends EnumAttribute[GenderValue] {

    case object Male extends GenderValue("Male")

    case object Female extends GenderValue("Female")

    case object Secret extends GenderValue("Secret")

    protected def all: Seq[GenderValue] = Seq[GenderValue](Male, Female, Secret)
  }

  implicit val format: OFormat[User] = Json.format[User]
}
