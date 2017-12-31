package controllers

import java.util.UUID

import io.circe.syntax._
import play.api.Logger
import play.api.mvc._
import model.{NewSubscriptionReq, Subscription}
import repositories.interpreter.CassandraSubscriptionRepositoryInterpreter
import services.interpreter.SubscriptionServiceInterpreter

class SubscriptionController(cc: ControllerComponents)
  extends CustomBaseController(cc) {

  import global.ApplicationExecutionContext._

  val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter

  def getSubscriptions() = Action.async { implicit req =>
    SubscriptionServiceInterpreter.findSubscriptions(userIdHeader)
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(subscriptions => Ok(subscriptions.asJson))
      .recover {
        case e =>
          Logger.error("Error retrieving subscriptions", e)
          InternalServerError("Error retrieving subscriptions")
      }
  }

  def createNewSubscription() = Action.async(circe.json[NewSubscriptionReq]) { implicit req =>
    val sub = Subscription(
      UUID.randomUUID(),
      req.body.name,
      userIdHeader,
      req.body.url,
      req.body.cssSelector,
      req.body.useHtmlExtractor
    )
    SubscriptionServiceInterpreter.storeSubscription(sub)
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(_ => Ok)
      .recover {
        case e =>
          Logger.error("Error storing a new subscription", e)
          InternalServerError("Error storing subscription")
      }
  }

}
