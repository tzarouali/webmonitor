package webmonitor.controllers

import java.util.UUID

import akka.stream.scaladsl.Flow
import cats.data.Validated
import play.api.libs.circe.Circe
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import webmonitor._
import webmonitor.repositories.interpreter.CassandraUserRepositoryInterpreter
import webmonitor.services.interpreter.UserServiceInterpreter

import scala.concurrent.Future

abstract class CustomBaseWebSocketController(cc: ControllerComponents)
  extends AbstractController(cc)
    with Circe {

  import webmonitor.global.WebSocketExecutionContext._

  protected final def securedWebsocket[In, Out](f: RequestHeader => Future[Either[Result, Flow[In, Out, _]]])(implicit transformer: MessageFlowTransformer[In, Out]): WebSocket = {
    WebSocket { request =>
      validateWebSocketRequest(request)
        .flatMap {
          case Left(r) =>
            Future(Left(r))
          case Right(_) =>
            f(request).map(_.right.map(transformer.transform))
        }
    }
  }

  private def validateWebSocketRequest(req: RequestHeader): Future[Either[Result, Unit]] = {
    val userIdQueryParam = req.queryString.get(WEBSOCKET_USER_QUERY_PARAM_NAME)
    val tokenQueryParam = req.queryString.get(WEBSOCKET_TOKEN_QUERY_PARAM_NAME)
    (userIdQueryParam, tokenQueryParam) match {
      case (Some(userId), Some(token)) if userId.nonEmpty && token.nonEmpty =>
        val userIdValue = userId.head
        val tokenValue = token.head
        tokenMatchesAndNotExpired(userIdValue, tokenValue)
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

}
