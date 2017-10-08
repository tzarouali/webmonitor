package webmonitor.app

import akka.http.scaladsl.server.Directives._

trait WebMonitorRoutes {

  val allRoutes = SubscriptionRoutes.subscriptionRoutes ~ UserRoutes.userRoutes

}
