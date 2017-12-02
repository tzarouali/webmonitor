package webmonitor.tasks

import java.time.LocalDateTime
import java.util.UUID

import org.jsoup.Jsoup
import play.api.Logger
import webmonitor.config.ApplicationConfigReader
import webmonitor.model.{Subscription, SubscriptionValue}
import webmonitor.repositories.interpreter.{CassandraSubscriptionFeedRepositoryInterpreter, CassandraSubscriptionRepositoryInterpreter}
import webmonitor.services.interpreter.{SubscriptionFeedServiceInterpreter, SubscriptionServiceInterpreter}

import scala.concurrent.Future

trait SubscriptionFeedReaderTask extends Task {

  import webmonitor.global.ApplicationExecutionContext._

  val subscriptionFeedRepo = CassandraSubscriptionFeedRepositoryInterpreter
  val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter

  def task(): Unit = {
    SubscriptionServiceInterpreter.findAllSubscriptions()
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(updateSubscriptions)
      .recover({
        case e =>
          Logger.error("Error updating the subscriptions", e)
      })
  }

  def updateSubscriptions(subs: Vector[Subscription]): Unit = {
    val groupSize = ApplicationConfigReader.config.constants.maxParallelTasks min subs.size
    subs.grouped(groupSize).foreach(_.par.foreach(updateSubscription))
  }

  def updateSubscription(sub: Subscription): Future[Unit] = {
    val feedValue = readSubscriptionValue(sub)
    val defaultTimeZone = ApplicationConfigReader.config.constants.defaultTimeZone
    val subscriptionValue = SubscriptionValue(UUID.randomUUID(), sub.id, feedValue, LocalDateTime.now(defaultTimeZone))
    SubscriptionFeedServiceInterpreter
      .storeSubscriptionFeedValue(sub.id, subscriptionValue)
      .run(subscriptionFeedRepo)
      .unsafeToFuture()
      .recover({
        case e =>
          Logger.error(s"Error updating the subscription ${sub.id}", e)
      })
  }

  def readSubscriptionValue(subscription: Subscription): String = {
    val doc = Jsoup.connect(subscription.url).ignoreContentType(true).ignoreHttpErrors(true).get()
    doc.select(subscription.jqueryExtractor).html()
  }

}

object SubscriptionFeedReaderTask extends SubscriptionFeedReaderTask
