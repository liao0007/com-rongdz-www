package models.user

import com.github.aselab.activerecord.{ActiveRecordCompanion, PlayFormSupport}
import com.mohiva.play.silhouette.api.Identity
import models.ActiveRecord
import models.core.District
import play.api.libs.json.{Json, OFormat}

/**
  * Created by liangliao on 4/13/16.
  */
case class ShipToAddress(
    override val id: Long,
    var userId: Long,
    var name: String,
    var districtId: Long,
    var address: String,
    var mobile: String,
    var isDefault: Boolean = false
) extends ActiveRecord
    with Identity {
  lazy val user: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[ShipToAddress.this.type, User] = belongsTo[User]
  lazy val district: _root_.com.github.aselab.activerecord.ActiveRecord.BelongsToAssociation[ShipToAddress.this.type, District] = belongsTo[District]
}

object ShipToAddress extends ActiveRecordCompanion[ShipToAddress] with PlayFormSupport[ShipToAddress] {
  implicit val format: OFormat[ShipToAddress] = Json.format[ShipToAddress]
}
