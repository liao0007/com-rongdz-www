package models.user

import java.util.Date

import com.github.aselab.activerecord.dsl._
import com.github.aselab.activerecord.{ActiveRecord, ActiveRecordCompanion, PlayFormSupport}
import com.mohiva.play.silhouette.api.Identity
import models.mall.SaleOrder
import models.user.User.UserGender
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
                 var gender: String = UserGender.Secret,
                 var birthday: Option[Date] = None,
                 var email: Option[String] = None,
                 var state: String
               ) extends ActiveRecord with Identity {
  lazy val tokens: ActiveRecord.HasManyAssociation[User.this.type, UserToken] = hasMany[UserToken]
  lazy val loginInfos: ActiveRecord.HasManyAssociation[User.this.type, LoginInfo] = hasMany[LoginInfo]
  lazy val toPermissions: ActiveRecord.HasManyAssociation[User.this.type, Permission] = hasMany[Permission]
  lazy val shipToAddresses: ActiveRecord.HasManyAssociation[User.this.type, ShipToAddress] =
    hasMany[ShipToAddress]
  lazy val orders: ActiveRecord.HasManyAssociation[User.this.type, SaleOrder] = hasMany[SaleOrder]
  lazy val tapes: ActiveRecord.HasManyAssociation[User.this.type, Tape] = hasMany[Tape]
}

object User extends ActiveRecordCompanion[User] with PlayFormSupport[User] {

  sealed class State(val name: String) extends EnumAttributeValue

  object UserState extends EnumAttribute[State] {

    case object Created extends State("Created")

    case object Activated extends State("Activated")

    case object Disabled extends State("Disabled")

    protected def all: Seq[State] = Seq[State](Created, Activated, Disabled)

  }

  sealed class Gender(val name: String) extends EnumAttributeValue

  object UserGender extends EnumAttribute[Gender] {

    case object Male extends Gender("Male")

    case object Female extends Gender("Female")

    case object Secret extends Gender("Secret")

    protected def all: Seq[Gender] = Seq[Gender](Male, Female, Secret)
  }

  implicit val format: OFormat[User] = Json.format[User]
}
