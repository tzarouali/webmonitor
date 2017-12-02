package webmonitor.config

import play.api._
import play.api.http.HttpErrorHandler
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.filters.cors.CORSConfig
import webmonitor.global.SecurityTokenFilter
import webmonitor.tasks.SubscriptionFeedReaderTask


class CustomAppLoader extends ApplicationLoader {
  private var components: CustomAppComponents = _

  override def load(context: ApplicationLoader.Context): Application = {
    val appenv = readEnvironment()
    println(s"Server configured for environment $appenv")
    components = new CustomAppComponents(context)
    startTasks()
    components.application
  }

  def readEnvironment(): String = sys.props.get(ApplicationEnvironmentVar.value) match {
    case Some(name) if name == LocalApplicationConfig.envName => name
    case Some(_) => sys.error("Environment not recognized!")
    case _ => sys.error("No environment available!")
  }

  def startTasks(): Unit = {
    SubscriptionFeedReaderTask.schedule()
  }

}

class CustomAppComponents(context: ApplicationLoader.Context)
  extends BuiltInComponentsFromContext(context)
    with play.filters.HttpFiltersComponents
    with play.filters.cors.CORSComponents
    with _root_.controllers.AssetsComponents {

  override def httpFilters: Seq[EssentialFilter] = Vector(corsFilter, SecurityTokenFilter)

  override lazy val corsConfig: CORSConfig = ApplicationConfigReader.config.corsConfig

  override lazy val httpErrorHandler: HttpErrorHandler = webmonitor.global.CustomErrorHandler

  lazy val subscriptionController = new _root_.webmonitor.controllers.SubscriptionController(controllerComponents)
  lazy val sessionController = new _root_.webmonitor.controllers.SessionController(controllerComponents)
  lazy val subscriptionFeedWebSocketController = new _root_.webmonitor.controllers.SubscriptionFeedWebSocketController(controllerComponents)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, sessionController, subscriptionController, subscriptionFeedWebSocketController)
}
