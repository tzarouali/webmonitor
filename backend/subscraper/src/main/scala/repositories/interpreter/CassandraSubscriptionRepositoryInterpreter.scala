package repositories.interpreter

import cats.Eval.always
import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import config.ExtendedApplicationConfigReader
import model.Subscription
import repositories.SubscriptionRepository

trait CassandraSubscriptionRepositoryInterpreter extends SubscriptionRepository[IO, Subscription] {

  import CassandraSubscriptionRepositoryInterpreter._
  import CassandraSubscriptionRepositoryInterpreter.subscriptionTable._

  override def findAllSubscriptions(): IO[Vector[Subscription]] =
    IO.fromFuture(always(
      subscriptionTable
        .select
        .fetch()
        .map(_.toVector)
    ))
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

