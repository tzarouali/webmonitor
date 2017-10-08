package webmonitor.app

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import webmonitor.model.Subscription
import webmonitor.repository.interpreter.CassandraSubscriptionRepositoryInterpreter
import webmonitor.service.interpreter.SubscriptionServiceInterpreter._

case class NewSubscription(url: String, jqueryExtractor: String)

trait SubscriptionRoutes extends ActorConfig with ErrorAccumulatingCirceSupport {

  val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter
  val subscriptionRoutes = pathPrefix("subscriptions") {
    path("user" / JavaUUID) { userId =>
      complete(
        findSubscriptions(userId).run(subscriptionRepo).unsafeRunSync().map(_.asJson)
          .recover({
            case e => e.getMessage.asJson
          })
      )
    } ~
    post {
      decodeRequest {
        entity(as[NewSubscription]) { newSub =>
          complete({
            val sub = Subscription(UUID.randomUUID(), newSub.url, newSub.jqueryExtractor, UUID.randomUUID())
            storeSubscription(sub).run(subscriptionRepo).unsafeRunSync().map(_.asJson)
              .recover({
                case e => e.getMessage.asJson
              })
          })
        }
      }
    }
  }

}
