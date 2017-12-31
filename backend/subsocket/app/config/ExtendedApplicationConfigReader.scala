package config

import play.filters.cors.CORSConfig

object ExtendedApplicationConfigReader extends ApplicationConfigReader[Unit, CORSConfig] {

  object localConfig extends LocalApplicationConfig[Unit, CORSConfig] {
    override val cassandraKeySpace: Unit = ()
    override val corsConfig: CORSConfig = CORSConfig.denyAll
      .withOriginsAllowed(_ => true)
      .withHeadersAllowed(_ => true)
      .withMethodsAllowed(_ => true)
  }

}
