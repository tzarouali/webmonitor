package repositories

import cats.data.OptionT

trait SubscriptionRepository[F[_], SUBSCRIPTION, ID] {
  def findAllSubscriptions(): F[Vector[SUBSCRIPTION]]

  def findSubscriptions(userId: ID): F[Vector[SUBSCRIPTION]]

  def storeSubscription(subscription: SUBSCRIPTION): F[Unit]

  def getSubscription(subscriptionId: ID): OptionT[F, SUBSCRIPTION]
}
