package webmonitor.controllers

import java.util.UUID

import akka.stream.scaladsl.{Flow, Sink, Source}
import io.circe.Json
import io.circe.syntax._
import play.api.http.websocket._
import play.api.libs.streams.AkkaStreams
import play.api.mvc.ControllerComponents
import play.api.mvc.WebSocket.MessageFlowTransformer
import webmonitor.model.SubscriptionValueNotAvailable
import webmonitor.repositories.interpreter.CassandraSubscriptionFeedRepositoryInterpreter
import webmonitor.services.interpreter.SubscriptionFeedServiceInterpreter

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class SubscriptionFeedWebSocketController(cc: ControllerComponents) extends CustomBaseWebSocketController(cc) {

  import webmonitor.global.WebSocketExecutionContext._

  val subscriptionFeedRepo = CassandraSubscriptionFeedRepositoryInterpreter

  implicit val jsonMessageFlowTransformer: MessageFlowTransformer[String, Json] = {
    (flow: Flow[String, Json, _]) => {
      AkkaStreams.bypassWith[Message, String, Message](Flow[Message] collect {
        case TextMessage(text) => Left(text)
        case BinaryMessage(_) =>
          Right(CloseMessage(
            Some(CloseCodes.Unacceptable),
            "This WebSocket only supports text frames"))
      })(flow.map(json => TextMessage.apply(json.toString())))
    }
  }

  def rootPath() = Action {
    Ok("")
  }

  def getSubscriptionFeedValue(subscriptionId: String) = securedWebsocket { _ =>
    val id = UUID.fromString(subscriptionId)

    val subscriptionFeedValue = SubscriptionFeedServiceInterpreter.getSubscriptionFeedValue(id)
      .run(subscriptionFeedRepo)
      .value
      .unsafeToFuture()
      .map {
        case Left(_) =>
          SubscriptionValueNotAvailable(id, "No data available").asJson
        case Right(subscriptionValue) =>
          subscriptionValue.asJson
      }

    val valueFeed = Source.fromFuture(subscriptionFeedValue).flatMapConcat(Source.tick(0 second, 2 second, _))

    Future(Right(Flow.fromSinkAndSource(Sink.ignore, valueFeed)))
  }

}
