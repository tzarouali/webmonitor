package scrapers

import config.ExtendedApplicationConfigReader
import scrapers.SubscriptionScraperTask.logger

import scala.concurrent.duration._

trait Task {

  import global.ApplicationExecutionContext._

  def task(): Unit

  final def schedule(): Unit = {
    val env = ExtendedApplicationConfigReader.readEnvironment()
    logger.info(s"Starting scraper for $env")
    system.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minute) {
      task()
    }
    ()
  }

}
