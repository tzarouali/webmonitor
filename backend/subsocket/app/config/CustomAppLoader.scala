package config

import play.api._
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.filters.cors.CORSConfig


class CustomAppLoader extends ApplicationLoader {
  private var components: CustomAppComponents = _

  override def load(context: ApplicationLoader.Context): Application = {
    val env = ExtendedApplicationConfigReader.readEnvironment()
    println(s"Server configured for environment $env")
    components = new CustomAppComponents(context)
    components.application
  }
}

class CustomAppComponents(context: ApplicationLoader.Context)
  extends BuiltInComponentsFromContext(context)
    with play.filters.HttpFiltersComponents
    with play.filters.cors.CORSComponents
    with _root_.controllers.AssetsComponents {

  override def httpFilters: Seq[EssentialFilter] = Vector(corsFilter)

  override lazy val corsConfig: CORSConfig = ExtendedApplicationConfigReader.config.corsConfig

  lazy val subscriptionFeedWebSocketController = new _root_.controllers.SubscriptionFeedWebSocketController(controllerComponents)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, subscriptionFeedWebSocketController)
}
