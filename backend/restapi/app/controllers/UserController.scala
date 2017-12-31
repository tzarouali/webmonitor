package controllers

import play.api.mvc.ControllerComponents
import repositories.interpreter.CassandraUserRepositoryInterpreter

class UserController(cc: ControllerComponents)
  extends CustomBaseController(cc) {

  val userRepo = CassandraUserRepositoryInterpreter

}
