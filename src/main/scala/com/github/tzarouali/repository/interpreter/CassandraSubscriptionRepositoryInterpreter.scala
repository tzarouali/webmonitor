package com.github.tzarouali.repository.interpreter

import cats.data.Kleisli
import cats.effect.IO
import com.github.tzarouali.RepoResult
import com.github.tzarouali.model.Subscription
import com.github.tzarouali.repository.SubscriptionRepository
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.keys.PartitionKey

trait CassandraSubscriptionRepositoryInterpreter extends SubscriptionRepository[RepoResult] {
  import CassandraSubscriptionRepositoryInterpreter._
  import CassandraSubscriptionRepositoryInterpreter.subscriptionTable._

  override def findSubscriptions(username: String) = Kleisli { _ =>
    IO {
      val asd = subscriptionTable.select.where(_.username eqs username).allowFiltering().fetch()
      asd.map(_.toVector)
    }
  }

  override def storeSubscription(subscription: Subscription) = Kleisli { c =>
    IO {
      ???
    }
  }
}

object CassandraSubscriptionRepositoryInterpreter extends CassandraSubscriptionRepositoryInterpreter {
  lazy val connector: CassandraConnection = ContactPoint.local.keySpace("test")

  class SubscriptionTable extends Table[SubscriptionTable, Subscription] with connector.Connector {
    object id extends BigDecimalColumn with PartitionKey
    object url extends StringColumn with PartitionKey
    object extractor extends StringColumn with PartitionKey
    object username extends StringColumn with PartitionKey
  }

  val subscriptionTable = new SubscriptionTable
}

