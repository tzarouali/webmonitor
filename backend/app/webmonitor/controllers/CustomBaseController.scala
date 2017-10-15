package webmonitor.controllers

import java.util.UUID

import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import webmonitor.{TOKEN_HEADER, USER_ID_HEADER}
import webmonitor.global.ApplicationExecutionContext

abstract class CustomBaseController(cc: ControllerComponents)
  extends AbstractController(cc)
    with ApplicationExecutionContext
    with Circe {

  implicit def tokenHeader()(implicit r: Request[AnyContent]): String = r.headers.get(TOKEN_HEADER).get
  implicit def userIdHeader()(implicit r: Request[AnyContent]): UUID = UUID.fromString(r.headers.get(USER_ID_HEADER).get)

}
