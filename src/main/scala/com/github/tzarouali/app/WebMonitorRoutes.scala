package com.github.tzarouali.app

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import scala.concurrent.ExecutionContext.Implicits.global


trait WebMonitorRoutes extends ErrorAccumulatingCirceSupport {

  import com.github.tzarouali.repository.interpreter.CassandraSubscriptionRepositoryInterpreter._

  val allRoutes = pathPrefix("subscriptions") {
    subscriptionRoutes
  }

  val subscriptionRoutes = path("user") {
    complete(
      findSubscriptions("u1").run(()).unsafeRunSync().map({
        case asd => asd.asJson
      }).recover({
        case e => e.getMessage.asJson
      })
    )
  }

}
