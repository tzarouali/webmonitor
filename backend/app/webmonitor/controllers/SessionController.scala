package webmonitor.controllers

import io.circe.syntax._
import play.api.Logger
import play.api.mvc.ControllerComponents
import webmonitor.model.{UserLogin, UserSessionData}
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.services.interpreter.UserServiceInterpreter

class SessionController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val userRepo = CassandraUserRepositoryInterpreter

  def login() = Action.async(circe.json[UserLogin]) { req =>
    val user = req.body
    UserServiceInterpreter.login(user.email, user.password)
      .run(userRepo)
      .unsafeToFuture()
      .map(_.fold(e => Unauthorized(e.err), s => Ok(s.asJson)))
      .recover({
        case e =>
          Logger.error("Error trying to login", e)
          InternalServerError("Error trying to login")
      })
  }

  def logout() = Action.async { implicit req =>
    UserServiceInterpreter.logout(UserSessionData(userIdHeader, tokenHeader))
      .run(userRepo)
      .unsafeToFuture()
      .map(_.fold(e => Unauthorized(e.err), _ => Ok("")))
      .recover({
        case e =>
          Logger.error("Error trying to logout", e)
          InternalServerError("Error trying to logout")
      })
  }

}
