package com.github.tzarouali.app

import akka.http.scaladsl.{Http, server}

import scala.util.{Failure, Success}

object WebMonitorServer extends ActorConfig with WebMonitorRoutes with App {

  val route: server.Route = allRoutes

  val port = 8000

  val host = "localhost"

  val binding = Http().bindAndHandle(route, host, port)

  binding onComplete  {
    case Success(_) =>
      println(s"Server bound to $host:$port")
    case Failure(e) =>
      println("Failed to bind: " + e.getMessage)
  }

}
