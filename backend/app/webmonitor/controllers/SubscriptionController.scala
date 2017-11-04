package webmonitor.controllers

import java.util.UUID

import io.circe.syntax._
import play.api.mvc.ControllerComponents
import webmonitor.model.{NewSubscriptionReq, Subscription}
import webmonitor.repositories.interpreter.CassandraSubscriptionRepositoryInterpreter
import webmonitor.services.interpreter.SubscriptionServiceInterpreter._

class SubscriptionController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter

  def getSubscriptions() = Action.async { implicit req =>
    findSubscriptions(userIdHeader)
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(subscriptions => Ok(subscriptions.asJson))
      .recover({
        case e => InternalServerError(e.getMessage)
      })
  }

  def createNewSubscription() = Action.async(circe.json[NewSubscriptionReq]) { req =>
    val sub = Subscription(UUID.randomUUID(), req.body.url, req.body.jqueryExtractor, UUID.randomUUID())
    storeSubscription(sub)
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(_ => Ok)
      .recover({
        case e => InternalServerError(e.getMessage)
      })
  }
}
