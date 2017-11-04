package webmonitor.repositories

trait SubscriptionRepository[F[_], SUBSCRIPTION, ID] {
  def findSubscriptions(userId: ID): F[Vector[SUBSCRIPTION]]

  def storeSubscription(subscription: SUBSCRIPTION): F[Unit]
}
