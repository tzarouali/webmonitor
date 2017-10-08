package webmonitor.app

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps

import scala.concurrent.ExecutionContext.Implicits.global


trait WebMonitorRoutes extends ErrorAccumulatingCirceSupport {

  import webmonitor.repository.interpreter.CassandraSubscriptionRepositoryInterpreter._

  val allRoutes = pathPrefix("subscriptions") {
    subscriptionRoutes
  }

  val subscriptionRoutes = path("user") {
    complete(
      findSubscriptions(UUID.randomUUID()).run(()).unsafeRunSync().map(_.asJson)
        .recover({
          case e => e.getMessage.asJson
        })
    )
  }

}
