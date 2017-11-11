package webmonitor.global

object SecuredApplicationUrls {
  lazy final val unsecuredEndpointUrls = Vector(webmonitor.controllers.routes.SessionController.login().url)
  lazy final val securedWebSocketEndpointUrls = Vector(webmonitor.controllers.routes.SubscriptionFeedWebSocketController.getSubscriptionFeedValue("").url)
}
