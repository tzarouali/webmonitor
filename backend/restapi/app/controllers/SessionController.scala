package controllers

import io.circe.syntax._
import play.api.Logger
import play.api.mvc.ControllerComponents
import model.{UserLogin, UserSessionData}
import repositories.interpreter.CassandraUserRepositoryInterpreter
import services.interpreter.UserServiceInterpreter

class SessionController(cc: ControllerComponents)
  extends CustomBaseController(cc) {

  import global.ApplicationExecutionContext._

  val userRepo = CassandraUserRepositoryInterpreter

  def login() = Action.async(circe.json[UserLogin]) { req =>
    val user = req.body
    UserServiceInterpreter.login(user.email, user.password)
      .run(userRepo)
      .value
      .unsafeToFuture()
      .map(_.fold(e => Unauthorized(e.err), sessionData => Ok(sessionData.asJson)))
      .recover({
        case e =>
          Logger.error("Error trying to login", e)
          InternalServerError("Error trying to login")
      })
  }

  def logout() = Action.async { implicit req =>
    UserServiceInterpreter.logout(UserSessionData(userIdHeader, tokenHeader))
      .run(userRepo)
      .value
      .unsafeToFuture()
      .map(_.fold(e => Unauthorized(e.err), _ => Ok("")))
      .recover({
        case e =>
          Logger.error("Error trying to logout", e)
          InternalServerError("Error trying to logout")
      })
  }

}
