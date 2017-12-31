package controllers

import akka.stream.scaladsl.Flow
import io.circe.Json
import play.api.http.websocket._
import play.api.libs.circe.Circe
import play.api.libs.streams.AkkaStreams
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

abstract class CustomBaseWebSocketController(cc: ControllerComponents)
  extends AbstractController(cc)
    with Circe {

  implicit val jsonMessageFlowTransformer: MessageFlowTransformer[String, Json] = {
    (flow: Flow[String, Json, _]) => {
      AkkaStreams.bypassWith[Message, String, Message](Flow[Message] collect {
        case TextMessage(text) => Left(text)
        case BinaryMessage(_) =>
          Right(CloseMessage(
            Some(CloseCodes.Unacceptable),
            "This WebSocket only supports text frames"))
      })(flow.map(json => TextMessage(json.toString())))
    }
  }

}
