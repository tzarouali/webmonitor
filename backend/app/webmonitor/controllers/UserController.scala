package webmonitor.controllers

import io.circe._
import io.circe.generic.semiauto._
import play.api.mvc.ControllerComponents
import webmonitor.model.User
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter

case class NewUser(name: String, email: String)

class UserController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val userRepo = CassandraUserRepositoryInterpreter

  implicit val userEncoder: Encoder[User] = deriveEncoder
  implicit val newUserDecoder: Decoder[NewUser] = deriveDecoder


}
