package webmonitor.repository.interpreter

import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.keys.PartitionKey
import webmonitor.config.ApplicationConfigReader
import webmonitor.model.Subscription
import webmonitor.repository.SubscriptionRepository

import scala.concurrent.Future

trait CassandraSubscriptionRepositoryInterpreter extends SubscriptionRepository[IO, Future, Subscription] {

  import CassandraSubscriptionRepositoryInterpreter._
  import CassandraSubscriptionRepositoryInterpreter.subscriptionTable._

  override def findSubscriptions(userId: UUID) = IO {
    subscriptionTable
      .select
      .where(_.userId eqs userId)
      .allowFiltering()
      .fetch()
      .map(_.toVector)
  }

  override def storeSubscription(subscription: Subscription) = IO {
    subscriptionTable
      .insert
      .value(_.id, subscription.id)
      .value(_.url, subscription.url)
      .value(_.jqueryExtractor, subscription.jqueryExtractor)
      .value(_.userId, subscription.userId)
      .future()
      .map(_ => ())
  }

}

object CassandraSubscriptionRepositoryInterpreter extends CassandraSubscriptionRepositoryInterpreter {
  lazy val connector: CassandraConnection = ApplicationConfigReader.config.cassandraKeySpace

  class Subscriptions extends Table[Subscriptions, Subscription] with connector.Connector {

    object id extends UUIDColumn with PrimaryKey

    object url extends StringColumn

    object jqueryExtractor extends StringColumn

    object userId extends UUIDColumn with PartitionKey

  }

  val subscriptionTable = new Subscriptions
}

