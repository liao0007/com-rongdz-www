package controllers

import auth.PasswordUpdateRequest
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by liangliao on 25/11/16.
  */
package object store {
  val passwordUpdateRequestForm = Form(
    mapping(
      "identifier" -> nonEmptyText,
      "password"   -> nonEmptyText
    )(PasswordUpdateRequest.apply)(PasswordUpdateRequest.unapply)
  )

  case class NameUpdateRequest(name: String)
  val nameUpdateRequestForm = Form(
    mapping(
      "name" -> nonEmptyText
    )(NameUpdateRequest.apply)(NameUpdateRequest.unapply)
  )

}
