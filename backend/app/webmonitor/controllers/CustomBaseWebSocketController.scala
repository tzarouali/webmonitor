package webmonitor.controllers

import akka.stream.scaladsl.Flow
import play.api.libs.circe.Circe
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import webmonitor.global.{WebSocketExecutionContext, WebSocketSecurityTokenFilter}

import scala.concurrent.Future

abstract class CustomBaseWebSocketController(cc: ControllerComponents)
  extends AbstractController(cc)
    with Circe
    with WebSocketExecutionContext {

  protected final def securedWebsocket[In, Out](f: RequestHeader => Future[Either[Result, Flow[In, Out, _]]])(implicit transformer: MessageFlowTransformer[In, Out]): WebSocket = {
    WebSocket { request =>
      WebSocketSecurityTokenFilter
        .validateWebSocketRequest(request)
        .flatMap {
          case Left(r) =>
            Future(Left(r))
          case Right(_) =>
            f(request).map(_.right.map(transformer.transform))
        }
    }
  }

}
