package webmonitor.controllers

import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import play.api.mvc.ControllerComponents
import webmonitor.model.User
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.services.interpreter.UserServiceInterpreter._

case class NewUser(name: String, email: String)

class UserController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val userRepo = CassandraUserRepositoryInterpreter

  implicit val userEncoder: Encoder[User] = deriveEncoder
  implicit val newUserDecoder: Decoder[NewUser] = deriveDecoder

  def getUserInfo(userId: UUID) = Action.async {
    findUser(userId).run(userRepo).unsafeRunSync().map({
      case Some(u) => Ok(u.asJson)
      case None => BadRequest(s"No user for ID $userId")
    }).recover({
      case e => InternalServerError(e.getMessage)
    })
  }

  def createNewUser() = Action.async(circe.json[NewUser]) { implicit req =>
    val user = User(UUID.randomUUID(), req.body.name, req.body.email)
    createUser(user).run(userRepo).unsafeRunSync().map(_ => Ok)
      .recover({
        case e => InternalServerError(e.getMessage)
      })
  }

}
