package webmonitor.repository.config

object RepositoryConfigReader {

  val config : RepositoryConfig = sys.props.get("appenv") match {
    case Some(name) if name == LocalRepositoryConfig.dbName => LocalRepositoryConfig
    case Some(_) => sys.error("Environment not recognized!")
    case _ => sys.error("No environment available!")
  }

}
