package repositories

trait SubscriptionFeedRepository[F[_], SUBSCRIPTION_VALUE, ID] {

  def storeSubscriptionFeedValue(subscriptionId: ID, value: SUBSCRIPTION_VALUE): F[Unit]

}
