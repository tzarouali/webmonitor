package webmonitor.controllers

import java.util.UUID

import akka.stream.scaladsl._
import io.circe.syntax._
import play.api.Logger
import play.api.mvc._
import webmonitor.model.{NewSubscriptionReq, Subscription}
import webmonitor.repositories.interpreter.CassandraSubscriptionRepositoryInterpreter
import webmonitor.services.interpreter.SubscriptionServiceInterpreter._

import scala.concurrent.duration._

class SubscriptionController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter


  def getSubscriptions() = Action.async { implicit req =>
    findSubscriptions(userIdHeader)
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(subscriptions => Ok(subscriptions.asJson))
      .recover({
        case e =>
          Logger.error("Error retrieving subscriptions", e)
          InternalServerError("Error retrieving subscriptions")
      })
  }

  def createNewSubscription() = Action.async(circe.json[NewSubscriptionReq]) { req =>
    val sub = Subscription(UUID.randomUUID(), req.body.url, req.body.jqueryExtractor, UUID.randomUUID())
    storeSubscription(sub)
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(_ => Ok)
      .recover({
        case e =>
          Logger.error("Error storing a new subscription", e)
          InternalServerError("Error storing subscription")
      })
  }

  def websocketTest = WebSocket.accept[String, String] { _ =>
    val eventualResult = getSubscriptionValue(UUID.fromString("72bf9b0c-6d52-44f8-9671-89ef6907480e"))
      .run(subscriptionRepo)
      .value
      .unsafeToFuture()
      .map(_.fold(_ => "ooooooooo", value => value.value))
    val asd = Source.fromFuture(eventualResult).flatMapConcat(asd => Source.tick(0 second, 2 second, asd))
    Flow.fromSinkAndSource(Sink.ignore, asd)
  }


}
