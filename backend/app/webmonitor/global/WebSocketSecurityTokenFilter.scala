package webmonitor.global

import java.util.UUID

import cats.data.Validated
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import webmonitor._
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.services.interpreter.UserServiceInterpreter

import scala.concurrent.Future

trait WebSocketSecurityTokenFilter extends WebSocketExecutionContext {

  def validateWebSocketRequest(req: RequestHeader): Future[Either[Result, Unit]] = {
    req.headers.get(WEBSOCKET_AUTHORIZATION_HEADER) match {
      case Some(userIdAndToken) if userIdAndToken.contains("_") =>
        val userId = userIdAndToken.split("_").head
        val token = userIdAndToken.split("_").last
        tokenMatchesAndNotExpired(userId, token)
          .map {
            case true => Right(())
            case false => Left(Unauthorized("Authorization failed."))
          }
      case _ => Future(Left(Unauthorized("Security headers not found.")))
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

  def securedWebSocketEndpoint(endpointUri: String): Boolean = {
    !SecuredApplicationUrls.unsecuredEndpointUrls.contains(endpointUri) && SecuredApplicationUrls.securedWebSocketEndpointUrls.contains(endpointUri)
  }

}

object WebSocketSecurityTokenFilter extends WebSocketSecurityTokenFilter
