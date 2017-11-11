package webmonitor.repositories

import cats.data.OptionT

trait SubscriptionFeedRepository[F[_], SUBSCRIPTION_VALUE, ID] {
  def getSubscriptionFeedValue(subscriptionId: ID): OptionT[F, SUBSCRIPTION_VALUE]
}
