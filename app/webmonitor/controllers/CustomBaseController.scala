package webmonitor.controllers

import java.util.concurrent.ForkJoinPool

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

abstract class CustomBaseController(cc: ControllerComponents) extends AbstractController(cc) with Circe {

  implicit final val system = ActorSystem()
  implicit final val materializer = ActorMaterializer()
  implicit final val ec = ExecutionContext.fromExecutor(new ForkJoinPool(8))

}
