package config

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}

object ExtendedApplicationConfigReader extends ApplicationConfigReader[CassandraConnection, Unit] {

  object localConfig extends LocalApplicationConfig[CassandraConnection, Unit] {
    override val cassandraKeySpace: CassandraConnection = ContactPoint.local.keySpace(dbName)
    override val corsConfig: Unit = ()
  }

}
