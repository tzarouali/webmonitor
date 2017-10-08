package webmonitor.app

import akka.http.scaladsl.Http
import webmonitor.config.{ApplicationEnvironmentVar, LocalApplicationConfig}

import scala.util.{Failure, Success}

object WebMonitorServer extends ActorConfig with WebMonitorRoutes with App {

  val route = allRoutes

  val port = 8000

  val host = "localhost"

  val binding = Http().bindAndHandle(route, host, port)

  binding onComplete  {
    case Success(_) =>
      val appenv = readEnvironment()
      println(s"Server bound to $host:$port for environment $appenv")
    case Failure(e) =>
      println("Failed to bind: " + e.getMessage)
  }

  def readEnvironment() = sys.props.get(ApplicationEnvironmentVar.value) match {
    case Some(name) if name == LocalApplicationConfig.envName => name
    case Some(_) => sys.error("Environment not recognized!")
    case _ => sys.error("No environment available!")
  }

}
