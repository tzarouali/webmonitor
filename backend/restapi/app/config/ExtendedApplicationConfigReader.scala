package config

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import play.filters.cors.CORSConfig

object ExtendedApplicationConfigReader extends ApplicationConfigReader[CassandraConnection, CORSConfig] {

  object localConfig extends LocalApplicationConfig[CassandraConnection, CORSConfig] {
    override val cassandraKeySpace: CassandraConnection = ContactPoint.local.keySpace(dbName)
    override val corsConfig: CORSConfig = CORSConfig.denyAll
      .withOriginsAllowed(_ => true)
      .withHeadersAllowed(_ => true)
      .withMethodsAllowed(_ => true)
  }

}
