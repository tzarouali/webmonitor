package global

import java.util.UUID

import app._
import cats.data.Validated
import play.api.libs.streams.Accumulator
import play.api.mvc.Results._
import play.api.mvc.{EssentialAction, EssentialFilter}
import repositories.interpreter.CassandraUserRepositoryInterpreter
import services.interpreter.UserServiceInterpreter

import scala.concurrent.Future

trait SecurityTokenFilter extends EssentialFilter {

  lazy final val unsecuredEndpointUrls = Vector(controllers.routes.SessionController.login().path())

  import global.ApplicationExecutionContext._

  override def apply(next: EssentialAction) = EssentialAction { req =>
    if (securedRestfulEndpoint(req.path)) {
      (req.headers.get(TOKEN_HEADER), req.headers.get(USER_ID_HEADER)) match {
        case (Some(token), Some(userId)) =>
          Accumulator.flatten(tokenMatchesAndNotExpired(userId, token).map({
            case true => next(req)
            case false => Accumulator.done(Unauthorized("Authorization failed."))
          }))
        case _ => Accumulator.done(Unauthorized("Security headers not found."))
      }
    } else {
      // accessing a non-secured endpoint
      next(req)
    }
  }

  private def tokenMatchesAndNotExpired(userId: String, token: String): Future[Boolean] = {
    val userRepo = CassandraUserRepositoryInterpreter
    Validated.catchNonFatal(UUID.fromString(userId)).toEither
      .fold(
        _ => Future.successful(false),
        uuid => UserServiceInterpreter.tokenValid(uuid, token)
          .run(userRepo)
          .value
          .unsafeToFuture()
          .map(_.fold(_ => false, b => b))
      )
  }

  private def securedRestfulEndpoint(endpointPath: String): Boolean =
    !unsecuredEndpointUrls.contains(endpointPath)

}

object SecurityTokenFilter extends SecurityTokenFilter
