package webmonitor.global

import java.util.UUID

import cats.data.Validated
import play.api.libs.streams.Accumulator
import play.api.mvc.Results._
import play.api.mvc.{EssentialAction, EssentialFilter}
import webmonitor._
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.services.interpreter.UserServiceInterpreter._

import scala.concurrent.Future

trait SecurityTokenFilter extends EssentialFilter with ApplicationExecutionContext {

  lazy final val UnsecuredEndpointUrls = Vector(webmonitor.controllers.routes.SessionController.login().url)

  override def apply(next: EssentialAction) = EssentialAction { req =>
    if (resourceSecured(req.uri)) {
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
        uuid => tokenValid(uuid, token)
          .run(userRepo)
          .value
          .unsafeToFuture()
          .map(_.fold(_ => false, b => b))
      )
  }

  private def resourceSecured(endpointUri: String): Boolean = {
    !UnsecuredEndpointUrls.contains(endpointUri)
  }
}

object SecurityTokenFilter extends SecurityTokenFilter
