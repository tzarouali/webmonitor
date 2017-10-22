package webmonitor.config

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import play.filters.cors.CORSConfig

object ApplicationEnvironmentVar {
  final val value = "appenv"
}

sealed trait ApplicationConfig {
  val envName: String
  val dbName: String
  val cassandraKeySpace: CassandraConnection
  val corsConfig: play.filters.cors.CORSConfig
}

case object LocalApplicationConfig extends ApplicationConfig {
  override val envName = "LOCAL-ENV"
  override val dbName = "local"
  override val cassandraKeySpace = ContactPoint.local.keySpace(dbName)
  override val corsConfig = CORSConfig.denyAll
    .withOriginsAllowed(_ => true)
    .withHeadersAllowed( _ => true)
    .withMethodsAllowed(_ => true)
}
