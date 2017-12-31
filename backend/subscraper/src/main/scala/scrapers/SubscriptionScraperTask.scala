package scrapers

import java.time.LocalDateTime
import java.util.UUID

import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.Logger
import config.ExtendedApplicationConfigReader
import io.circe.syntax._
import model.{Subscription, SubscriptionValue}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.jsoup.Jsoup
import repositories.interpreter.{CassandraSubscriptionFeedRepositoryInterpreter, CassandraSubscriptionRepositoryInterpreter}
import services.interpreter.{SubscriptionFeedServiceInterpreter, SubscriptionServiceInterpreter}

import scala.util.Success

object SubscriptionScraperTask extends Task with App {

  final lazy val logger = Logger("SubscriptionScraperTask")

  import global.ApplicationExecutionContext._

  lazy val subscriptionFeedRepo = CassandraSubscriptionFeedRepositoryInterpreter
  lazy val subscriptionRepo = CassandraSubscriptionRepositoryInterpreter

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(ExtendedApplicationConfigReader.config.kafkaServer)

  def task(): Unit = {
    logger.info("Starting scraping tasks")
    SubscriptionServiceInterpreter.findAllSubscriptions()
      .run(subscriptionRepo)
      .unsafeToFuture()
      .map(updateSubscriptions)
      .recover({
        case e =>
          logger.error("Error updating the subscriptions", e)
      })
    ()
  }

  def updateSubscriptions(subs: Vector[Subscription]): Unit = {
    val groupSize = ExtendedApplicationConfigReader.config.constants.maxParallelTasks min subs.size

    logger.debug(s"Max parallel scraping tasks is $groupSize")

    subs.grouped(groupSize).foreach(_.par.foreach { sub =>
      val feedValue = readSubscriptionValue(sub)
      val defaultTimeZone = ExtendedApplicationConfigReader.config.constants.defaultTimeZone
      val subscriptionValue = SubscriptionValue(UUID.randomUUID(), sub.id, feedValue, LocalDateTime.now(defaultTimeZone))
      SubscriptionFeedServiceInterpreter
        .storeSubscriptionFeedValue(sub.id, subscriptionValue)
        .run(subscriptionFeedRepo)
        .unsafeToFuture()
        .andThen({
          case Success(_) =>
            publishSubscription(subscriptionValue)
        })
        .recover({
          case e =>
            logger.error(s"Error updating the subscription ${sub.id}", e)
        })
    })
  }

  def readSubscriptionValue(subscription: Subscription): String = {
    val doc = Jsoup.connect(subscription.url).ignoreContentType(true).ignoreHttpErrors(true).get()
    if (subscription.useHtmlExtractor)
      doc.select(subscription.cssSelector).html()
    else
      doc.select(subscription.cssSelector).text()
  }

  def publishSubscription(subscriptionValue: SubscriptionValue) = {
    logger.debug(s"Publishing to Kafka topic ${subscriptionValue.subscriptionId}")
    Source(List(subscriptionValue))
      .map { elem =>
        new ProducerRecord[String, String](subscriptionValue.subscriptionId.toString, elem.asJson.noSpaces)
      }
      .runWith(Producer.plainSink(producerSettings))
  }

  schedule()
}
