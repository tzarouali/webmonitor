package webmonitor.repositories

import cats.data.OptionT

trait SubscriptionFeedRepository[F[_], SUBSCRIPTION_VALUE, ID] {
  def getSubscriptionFeedValue(subscriptionId: ID): OptionT[F, SUBSCRIPTION_VALUE]

  def storeSubscriptionFeedValue(subscriptionId: ID, value: SUBSCRIPTION_VALUE): F[Unit]
}
