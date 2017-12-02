package webmonitor.services

import cats.data.{EitherT, Reader}
import webmonitor.model.SubscriptionFeedValueNotFound
import webmonitor.repositories.SubscriptionFeedRepository

trait SubscriptionFeedService[F[_], ID, SUBSCRIPTION_VALUE] {

  def getSubscriptionFeedValue(subscriptionId: ID): Reader[SubscriptionFeedRepository[F, SUBSCRIPTION_VALUE, ID], EitherT[F, SubscriptionFeedValueNotFound, SUBSCRIPTION_VALUE]]

  def storeSubscriptionFeedValue(subscriptionId: ID, subscriptionValue: SUBSCRIPTION_VALUE): Reader[SubscriptionFeedRepository[F, SUBSCRIPTION_VALUE, ID], F[Unit]]

}
