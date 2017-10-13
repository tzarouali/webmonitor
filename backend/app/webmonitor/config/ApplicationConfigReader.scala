package webmonitor.config

object ApplicationConfigReader {

  lazy val config : ApplicationConfig = sys.props.get(ApplicationEnvironmentVar.value).get match {
    case LocalApplicationConfig.envName => LocalApplicationConfig
    case e => sys.error(s"No application configuration available for environment $e")
  }

}
