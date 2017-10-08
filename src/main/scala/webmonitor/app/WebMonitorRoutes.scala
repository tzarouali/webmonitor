package webmonitor.app

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import webmonitor.model.Subscription

import scala.concurrent.ExecutionContext.Implicits.global


trait WebMonitorRoutes extends ErrorAccumulatingCirceSupport {

  import webmonitor.repository.interpreter.CassandraSubscriptionRepositoryInterpreter._

  val subscriptionRoutes =
    path("user") {
      complete(
        findSubscriptions(UUID.randomUUID()).run(()).unsafeRunSync().map(_.asJson)
          .recover({
            case e => e.getMessage.asJson
          })
      )
    } ~
    path("store") {
      complete({
        val sub = Subscription(UUID.randomUUID(), "the_url", "the_extractor", UUID.randomUUID())
        storeSubscription(sub).run(()).unsafeRunSync().map(_.asJson)
          .recover({
            case e => e.getMessage.asJson
          })
      })
    }

  val allRoutes = pathPrefix("subscriptions") {
    subscriptionRoutes
  }

}
