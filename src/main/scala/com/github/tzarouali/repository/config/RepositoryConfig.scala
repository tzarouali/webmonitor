package com.github.tzarouali.repository.config

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}

trait RepositoryConfig {
  val cassandraKeySpace: CassandraConnection
  val dbName: String
}

case object LocalRepositoryConfig extends RepositoryConfig {
  override val dbName = "local"
  override val cassandraKeySpace = ContactPoint.local.keySpace(dbName)
}
