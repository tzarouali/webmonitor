package webmonitor.controllers

import java.util.UUID

import io.circe.syntax._
import play.api.Logger
import play.api.mvc._
import webmonitor.global.ApplicationExecutionContext
import webmonitor.model.{NewSubscriptionReq, Subscription}
import webmonitor.repositories.interpreter.CassandraSubscriptionRepositoryInterpreter
import webmonitor.services.interpreter.SubscriptionServiceInterpreter

class SubscriptionController(cc: ControllerComponents)
  extends CustomBaseController(cc)
    with ApplicationExecutionContext {

  val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter

  def getSubscriptions() = Action.async { implicit req =>
    SubscriptionServiceInterpreter.findSubscriptions(userIdHeader)
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
    SubscriptionServiceInterpreter.storeSubscription(sub)
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(_ => Ok)
      .recover({
        case e =>
          Logger.error("Error storing a new subscription", e)
          InternalServerError("Error storing subscription")
      })
  }

}
