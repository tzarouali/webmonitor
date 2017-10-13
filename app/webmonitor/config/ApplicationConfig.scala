package webmonitor.config

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}

object ApplicationEnvironmentVar {
  final val value = "appenv"
}

sealed trait ApplicationConfig {
  val envName: String
  val dbName: String
  val cassandraKeySpace: CassandraConnection
}

case object LocalApplicationConfig extends ApplicationConfig {
  override val envName = "LOCAL-ENV"
  override val dbName = "local"
  override val cassandraKeySpace = ContactPoint.local.keySpace(dbName)
}
