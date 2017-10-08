package webmonitor.repository

import java.util.UUID

trait SubscriptionRepository[F[_], G[_], SUBSCRIPTION] {
  def findSubscriptions(userId: UUID): F[G[Vector[SUBSCRIPTION]]]

  def storeSubscription(subscription: SUBSCRIPTION): F[G[Unit]]
}
