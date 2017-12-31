package config

import global.SecurityTokenFilter
import play.api._
import play.api.http.HttpErrorHandler
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

  override def httpFilters: Seq[EssentialFilter] = Vector(corsFilter, SecurityTokenFilter)

  override lazy val corsConfig: CORSConfig = ExtendedApplicationConfigReader.config.corsConfig

  override lazy val httpErrorHandler: HttpErrorHandler = global.CustomErrorHandler

  lazy val subscriptionController = new _root_.controllers.SubscriptionController(controllerComponents)
  lazy val sessionController = new _root_.controllers.SessionController(controllerComponents)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, sessionController, subscriptionController)
}
