package controllers

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink}
import com.typesafe.scalalogging.Logger
import config.ExtendedApplicationConfigReader
import io.circe.syntax._
import io.circe.{parser => CP}
import model.SubscriptionFeedValueParsingError
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.mvc.{ControllerComponents, WebSocket}

import scala.concurrent.Future

class SubscriptionFeedWebSocketController(cc: ControllerComponents) extends CustomBaseWebSocketController(cc) {

  lazy val logger = Logger("SubscriptionFeedWebSocketController")

  import global.WebSocketExecutionContext._

  lazy val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(ExtendedApplicationConfigReader.config.kafkaServer)

  def getSubscriptionFeedValue(subscriptionId: String) = WebSocket.acceptOrResult { _ =>
    val source = Consumer.plainSource(consumerSettings.withGroupId(subscriptionId), Subscriptions.topics(subscriptionId))
      .map(msg => {
        logger.debug(s"Subscription value received: ${msg.value()}")
        CP.parse(msg.value()) match {
          case Right(json) => json
          case Left(err) => SubscriptionFeedValueParsingError(err.message).asJson
        }
      })
    Future {
      Right(Flow.fromSinkAndSource(Sink.ignore, source))
    }
  }

}
