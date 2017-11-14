package webmonitor.global

import java.util.UUID

import cats.data.Validated
import play.api.libs.streams.Accumulator
import play.api.mvc.Results._
import play.api.mvc.{EssentialAction, EssentialFilter}
import webmonitor._
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.services.interpreter.UserServiceInterpreter

import scala.concurrent.Future

trait SecurityTokenFilter extends EssentialFilter {

  lazy final val unsecuredEndpointUrls = Vector(webmonitor.controllers.routes.SessionController.login().path())
  lazy final val securedWebSocketEndpointUrls = Vector(webmonitor.controllers.routes.SubscriptionFeedWebSocketController.rootPath().url)

  import webmonitor.global.ApplicationExecutionContext._

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
    notUnsecuredRequest(endpointPath) && notWebSocketRequest(endpointPath)

  private def notUnsecuredRequest(endpointPath: String): Boolean =
    !unsecuredEndpointUrls.contains(endpointPath)

  private def notWebSocketRequest(endpointPath: String): Boolean =
    !securedWebSocketEndpointUrls.exists(endpointPath.startsWith)

}

object SecurityTokenFilter extends SecurityTokenFilter
