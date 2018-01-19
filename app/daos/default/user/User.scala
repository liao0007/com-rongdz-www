package daos.default.user

import java.util.{Date, UUID}

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import com.mohiva.play.silhouette.api.Identity
import daos.{ActiveRecord, IterableAttribute}
import daos.default.mall.SaleOrder
import play.api.libs.json.{Json, OFormat}
import com.github.aselab.activerecord.dsl._
import daos.default.user.User.UserGender

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
) extends ActiveRecord
    with Identity {
  lazy val tokens: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[User.this.type, Token]               = hasMany[Token]
  lazy val loginInfos: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[User.this.type, LoginInfo]       = hasMany[LoginInfo]
  lazy val toPermissions: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[User.this.type, ToPermission] = hasMany[ToPermission]
  lazy val shipToAddresses: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[User.this.type, ShipToAddress] =
    hasMany[ShipToAddress]
  lazy val orders: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[User.this.type, SaleOrder] = hasMany[SaleOrder]
  lazy val tapes: _root_.com.github.aselab.activerecord.ActiveRecord.HasManyAssociation[User.this.type, Tape]       = hasMany[Tape]
}

object User extends ActiveRecordCompanion[User] with PlayFormSupport[User] {

  sealed class State(val name: String)
  object UserState extends IterableAttribute[State] {
    case object Created   extends State("新建")
    case object Activated extends State("激活")
    case object Disabled  extends State("作废")
    protected def all: Seq[State]                     = Seq[State](Created, Activated, Disabled)
    implicit def fromString(x: String): Option[State] = all.find(_.toString == x)
    implicit def toString(state: State): String       = state.toString
  }

  sealed class Gender(val name: String)
  object UserGender extends IterableAttribute[Gender] {
    case object Male   extends Gender("先生")
    case object Female extends Gender("女士")
    case object Secret extends Gender("保密")
    protected def all: Seq[Gender]                     = Seq[Gender](Male, Female, Secret)
    implicit def fromString(x: String): Option[Gender] = all.find(_.toString == x)
    implicit def toString(gender: Gender): String      = gender.toString
  }

  implicit val format: OFormat[User] = Json.format[User]
}
