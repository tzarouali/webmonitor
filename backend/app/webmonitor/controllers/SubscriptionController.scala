package webmonitor.controllers

import java.util.UUID

import io.circe.syntax._
import play.api.mvc.ControllerComponents
import webmonitor.model.{NewSubscriptionReq, Subscription}
import webmonitor.repositories.interpreter.CassandraSubscriptionRepositoryInterpreter
import webmonitor.services.interpreter.SubscriptionServiceInterpreter._

class SubscriptionController(cc: ControllerComponents) extends CustomBaseController(cc) {

  val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter

  def getSubscriptions(userId: UUID) = Action.async {
    findSubscriptions(userId)
      .run(subscriptionRepo)
      .unsafeRunSync()
      .map(subscriptions => Ok(subscriptions.asJson))
      .recover({
        case e => InternalServerError(e.getMessage)
      })
  }

  def createNewSubscription() = Action.async(circe.json[NewSubscriptionReq]) { req =>
    val sub = Subscription(UUID.randomUUID(), req.body.url, req.body.jqueryExtractor, UUID.randomUUID())
    storeSubscription(sub).run(subscriptionRepo).unsafeRunSync().map(_ => Ok)
      .recover({
        case e => InternalServerError(e.getMessage)
      })
  }
}
