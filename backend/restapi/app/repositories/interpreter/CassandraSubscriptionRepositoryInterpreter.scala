package repositories.interpreter

import java.util.UUID

import cats.Eval.always
import cats.data.OptionT
import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import config.ExtendedApplicationConfigReader
import model.Subscription
import repositories.SubscriptionRepository

trait CassandraSubscriptionRepositoryInterpreter extends SubscriptionRepository[IO, Subscription, UUID] {

  import CassandraSubscriptionRepositoryInterpreter._
  import CassandraSubscriptionRepositoryInterpreter.subscriptionTable._

  override def findAllSubscriptions(): IO[Vector[Subscription]] =
    IO.fromFuture(always(
      subscriptionTable
        .select
        .fetch()
        .map(_.toVector)
    ))

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
        .value(_.name, subscription.name)
        .value(_.userId, subscription.userId)
        .value(_.url, subscription.url)
        .value(_.cssSelector, subscription.cssSelector)
        .value(_.useHtmlExtractor, subscription.useHtmlExtractor)
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
  lazy val connector: CassandraConnection = ExtendedApplicationConfigReader.config.cassandraKeySpace

  class Subscriptions extends Table[Subscriptions, Subscription] with connector.Connector {

    object id extends UUIDColumn with PartitionKey

    object name extends StringColumn

    object userId extends UUIDColumn with PartitionKey {
      override def name: String = "user_id"
    }

    object url extends StringColumn

    object cssSelector extends StringColumn {
      override def name: String = "css_selector"
    }

    object useHtmlExtractor extends BooleanColumn {
      override def name: String = "use_html_extractor"
    }

  }

  val subscriptionTable = new Subscriptions
}

