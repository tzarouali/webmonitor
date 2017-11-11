package webmonitor.repositories.interpreter

import cats.Eval.always
import cats.data.OptionT
import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import webmonitor.config.ApplicationConfigReader
import webmonitor.model.Subscription
import webmonitor.repositories.SubscriptionRepository

trait CassandraSubscriptionRepositoryInterpreter extends SubscriptionRepository[IO, Subscription, UUID] {

  import CassandraSubscriptionRepositoryInterpreter._
  import CassandraSubscriptionRepositoryInterpreter.subscriptionTable._

  override def findSubscriptions(userId: UUID): IO[Vector[Subscription]] =
    IO.fromFuture(always(
      subscriptionTable
        .select
        .where(_.userId eqs userId)
        .allowFiltering()
        .fetch()
        .map(_.toVector)
    ))

  override def storeSubscription(subscription: Subscription): IO[Unit] =
    IO.fromFuture(always(
      subscriptionTable
        .insert
        .value(_.id, subscription.id)
        .value(_.url, subscription.url)
        .value(_.jqueryExtractor, subscription.jqueryExtractor)
        .value(_.userId, subscription.userId)
        .future()
        .map(_ => ())
    ))

  override def getSubscription(subscriptionId: UUID): OptionT[IO, Subscription] =
    OptionT(
      IO.fromFuture(always(
        subscriptionTable
          .select
          .where(_.id eqs subscriptionId)
          .one()
      ))
    )
}

object CassandraSubscriptionRepositoryInterpreter extends CassandraSubscriptionRepositoryInterpreter {
  lazy val connector: CassandraConnection = ApplicationConfigReader.config.cassandraKeySpace

  class Subscriptions extends Table[Subscriptions, Subscription] with connector.Connector {

    object id extends UUIDColumn with PrimaryKey

    object url extends StringColumn

    object jqueryExtractor extends StringColumn {
      override def name: String = "jquery_extractor"
    }

    object userId extends UUIDColumn with PartitionKey {
      override def name: String = "user_id"
    }

  }

  val subscriptionTable = new Subscriptions
}

