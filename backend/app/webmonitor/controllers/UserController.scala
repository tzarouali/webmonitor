package webmonitor.controllers

import play.api.mvc.ControllerComponents
import webmonitor.global.ApplicationExecutionContext
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter

class UserController(cc: ControllerComponents)
  extends CustomBaseController(cc)
    with ApplicationExecutionContext {

  val userRepo = CassandraUserRepositoryInterpreter

}
