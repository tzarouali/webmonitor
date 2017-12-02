package webmonitor.tasks

import scala.concurrent.duration._

trait Task {

  import webmonitor.global.ApplicationExecutionContext._

  def task(): Unit

  final def schedule(): Unit = {
    system.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minute) {
      task()
    }
  }

  final def scheduleEvery(interval: FiniteDuration): Unit = {
    system.scheduler.schedule(initialDelay = 0.seconds, interval = interval) {
      task()
    }
  }

}
