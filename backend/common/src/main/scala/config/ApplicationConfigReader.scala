package config

trait ApplicationConfigReader[A, B] {
  val localConfig: LocalApplicationConfig[A, B]

  lazy val config : ApplicationConfig[A, B] = {
    val env = sys.props.get(ApplicationEnvironmentVar.value).get
    if (localConfig.envName == env) {
      localConfig
    } else {
      sys.error(s"No application configuration available for environment $env")
    }
  }

  final def readEnvironment(): String = sys.props.get(ApplicationEnvironmentVar.value) match {
    case Some(name) if name == localConfig.envName => name
    case Some(_) => sys.error("Environment not recognized!")
    case _ => sys.error("No environment available!")
  }

}
