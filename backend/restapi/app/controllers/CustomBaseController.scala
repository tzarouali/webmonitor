package controllers

import java.util.UUID

import app._
import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, ControllerComponents, RequestHeader}

abstract class CustomBaseController(cc: ControllerComponents)
  extends AbstractController(cc)
    with Circe {

  implicit def tokenHeader()(implicit r: RequestHeader): String = r.headers.get(TOKEN_HEADER).get
  implicit def userIdHeader()(implicit r: RequestHeader): UUID = UUID.fromString(r.headers.get(USER_ID_HEADER).get)

}
