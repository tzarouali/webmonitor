package webmonitor.app

import java.util.concurrent.ForkJoinPool

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait ActorConfig {
  implicit final val system = ActorSystem()
  implicit final val materializer = ActorMaterializer()
  implicit final val ec = ExecutionContext.fromExecutor(new ForkJoinPool(1))
}
