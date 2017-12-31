package config

import java.time.ZoneId

object ApplicationEnvironmentVar {
  final val value = "appenv"
}

trait ApplicationConfig[CASSANDRA_CONNECTION, CORS_CONFIG] {
  val constants = Constants
  val envName: String
  val dbName: String
  val cassandraKeySpace: CASSANDRA_CONNECTION
  val corsConfig: CORS_CONFIG
  val kafkaServer: String
}

abstract case class LocalApplicationConfig[CASSANDRA_CONNECTION, CORS_CONFIG]() extends ApplicationConfig[CASSANDRA_CONNECTION, CORS_CONFIG] {
  override val envName = "LOCAL-ENV"
  override val dbName = "local"
  override val kafkaServer: String = "localhost:9092"
}

object Constants {
  final val defaultTimeZone = ZoneId.of("UTC")
  final val maxParallelTasks = 10
  final val wsAuthReqTopic = "ws-auth-req"
  final val wsAuthRespTopic = "ws-auth-resp"
}
