package webmonitor.app

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import webmonitor.model.User
import webmonitor.repository.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.service.interpreter.UserServiceInterpreter._

case class NewUser(name: String, email: String)

trait UserRoutes extends ActorConfig with FailFastCirceSupport {

  implicit val fooDecoder: Decoder[NewUser] = deriveDecoder[NewUser]
  implicit val fooDecoder2: Encoder[User] = deriveEncoder[User]

  val userRepo = CassandraUserRepositoryInterpreter
  val userRoutes = pathPrefix("users") {
    path(JavaUUID) { userId =>
      complete(
        findUser(userId).run(userRepo).unsafeRunSync().map({
          case Some(u) => u.asJson
          case None => s"No user for ID $userId".asJson
        }).recover({
          case e => e.getMessage.asJson
        })
      )
    } ~
    post {
      decodeRequest {
        entity(as[NewUser]) { newUser =>
          complete({
            val user = User(UUID.randomUUID(), newUser.name, newUser.email)
            createUser(user).run(userRepo).unsafeRunSync().map(_.asJson)
              .recover({
                case e => e.getMessage.asJson
              })
          })
        }
      }
    }
  }

}

object UserRoutes extends UserRoutes
