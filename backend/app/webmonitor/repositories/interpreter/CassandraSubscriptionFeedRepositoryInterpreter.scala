package webmonitor.repositories.interpreter

import java.time.LocalDateTime
import java.util.UUID

import cats.Eval._
import cats.data.OptionT
import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.jdk8._
import webmonitor.config.ApplicationConfigReader
import webmonitor.model.SubscriptionValue
import webmonitor.repositories.SubscriptionFeedRepository

trait CassandraSubscriptionFeedRepositoryInterpreter extends SubscriptionFeedRepository[IO, SubscriptionValue, UUID] {

  import CassandraSubscriptionFeedRepositoryInterpreter._
  import CassandraSubscriptionFeedRepositoryInterpreter.subscriptionFeedTable._

  override def getSubscriptionFeedValue(subscriptionId: UUID): OptionT[IO, SubscriptionValue] =
    OptionT(
      IO.fromFuture(always(
        subscriptionFeedTable
          .select
          .where(_.subscriptionId eqs subscriptionId)
          .allowFiltering()
          .one()
      ))
    )

  override def storeSubscriptionFeedValue(subscriptionId: UUID, value: SubscriptionValue): IO[Unit] = {
    IO.fromFuture(always(
      subscriptionFeedTable
        .insert
        .value(_.id, value.id)
        .value(_.subscriptionId, value.subscriptionId)
        .value(_.value, value.value)
        .value(_.lastUpdated, value.lastUpdated)
        .future()
        .map(_ => ())
    ))
  }
}

object CassandraSubscriptionFeedRepositoryInterpreter extends CassandraSubscriptionFeedRepositoryInterpreter {
  lazy val connector: CassandraConnection = ApplicationConfigReader.config.cassandraKeySpace

  class SubscriptionFeedValues extends Table[SubscriptionFeedValues, SubscriptionValue] with connector.Connector {

    override def tableName: String = "subscription_feed_values"

    object id extends UUIDColumn with PartitionKey

    object subscriptionId extends UUIDColumn with PartitionKey {
      override def name: String = "subscription_id"
    }

    object value extends StringColumn

    object lastUpdated extends Col[LocalDateTime] with ClusteringOrder with Descending {
      override def name: String = "last_updated"
    }

  }

  val subscriptionFeedTable = new SubscriptionFeedValues
}
