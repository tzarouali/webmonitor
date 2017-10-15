package webmonitor.controllers

import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import play.api.mvc.ControllerComponents
import webmonitor._
import webmonitor.model.{UserLogin, UserSessionData}
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.services.interpreter.UserServiceInterpreter

class SessionController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val userRepo = CassandraUserRepositoryInterpreter

  implicit val sessionDataEncoder: Encoder[UserSessionData] = deriveEncoder
  implicit val userLoginDataDecoder: Decoder[UserLogin] = deriveDecoder

  def login() = Action.async(circe.json[UserLogin]) { req =>
    val user = req.body
    UserServiceInterpreter.login(user.email, user.password)
      .run(userRepo)
      .unsafeToFuture()
      .map(_.fold(e => Unauthorized(e.err), s => Ok(s.asJson)))
  }

  def logout(userId: UUID) = Action.async { req =>
    // the security filter would not allow requests without a proper token,
    // so we're safe getting the value here
    val token = req.headers.get(TOKEN_HEADER).get
    UserServiceInterpreter.logout(UserSessionData(userId, token))
      .run(userRepo)
      .unsafeToFuture()
      .map(_.fold(e => Unauthorized(e.err), _ => Ok("")))
  }

}
