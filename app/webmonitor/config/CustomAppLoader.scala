package webmonitor.config

import play.api._
import play.api.routing.Router


class CustomAppLoader extends ApplicationLoader {
  private var components: CustomAppComponents = _

  override def load(context: ApplicationLoader.Context): Application = {
    val appenv = readEnvironment()
    println(s"Server configured for environment $appenv")
    components = new CustomAppComponents(context)
    components.application
  }

  def readEnvironment(): String = sys.props.get(ApplicationEnvironmentVar.value) match {
    case Some(name) if name == LocalApplicationConfig.envName => name
    case Some(_) => sys.error("Environment not recognized!")
    case _ => sys.error("No environment available!")
  }

}

class CustomAppComponents(context: ApplicationLoader.Context)
  extends BuiltInComponentsFromContext(context)
    with play.filters.HttpFiltersComponents
    with _root_.controllers.AssetsComponents {

  lazy val subscriptionController = new _root_.webmonitor.controllers.SubscriptionController(controllerComponents)
  lazy val userController = new _root_.webmonitor.controllers.UserController(controllerComponents)
  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, userController, subscriptionController)
}
