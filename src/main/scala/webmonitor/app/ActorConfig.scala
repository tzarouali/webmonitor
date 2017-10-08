package webmonitor.app

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer


trait ActorConfig {
  implicit final val system = ActorSystem()
  implicit final val materializer = ActorMaterializer()
  implicit final val dbDispatcher = system.dispatcher
}
