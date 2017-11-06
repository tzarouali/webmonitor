package webmonitor.repositories

import cats.data.OptionT

trait SubscriptionRepository[F[_], SUBSCRIPTION, ID] {
  def findSubscriptions(userId: ID): F[Vector[SUBSCRIPTION]]

  def storeSubscription(subscription: SUBSCRIPTION): F[Unit]

  def getSubscription(subscriptionId: ID): OptionT[F, SUBSCRIPTION]
}
