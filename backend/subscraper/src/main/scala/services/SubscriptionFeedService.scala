package services

import cats.data.Reader
import repositories.SubscriptionFeedRepository

trait SubscriptionFeedService[F[_], ID, SUBSCRIPTION_VALUE] {

  def storeSubscriptionFeedValue(subscriptionId: ID, subscriptionValue: SUBSCRIPTION_VALUE): Reader[SubscriptionFeedRepository[F, SUBSCRIPTION_VALUE, ID], F[Unit]]

}
