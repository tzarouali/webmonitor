package webmonitor.controllers

import play.api.mvc.ControllerComponents
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter

class UserController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val userRepo = CassandraUserRepositoryInterpreter

}
