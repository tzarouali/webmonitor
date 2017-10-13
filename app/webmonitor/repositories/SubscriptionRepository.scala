package webmonitor.repositories

trait SubscriptionRepository[F[_], G[_], SUBSCRIPTION, ID] {
  def findSubscriptions(userId: ID): F[G[Vector[SUBSCRIPTION]]]

  def storeSubscription(subscription: SUBSCRIPTION): F[G[Unit]]
}
