package webmonitor.services

import cats.data.{EitherT, Kleisli, Reader}
import webmonitor.model.SubscriptionNotFound
import webmonitor.repositories.SubscriptionRepository

trait SubscriptionService[F[_], ID, SUBSCRIPTION, SUBSCRIPTION_VALUE] {

  def findSubscriptions(userId: ID): Kleisli[F, SubscriptionRepository[F, SUBSCRIPTION, ID], Vector[SUBSCRIPTION]]

  def storeSubscription(subscription: SUBSCRIPTION): Kleisli[F, SubscriptionRepository[F, SUBSCRIPTION, ID], Unit]

  def getSubscriptionValue(subscriptionId: ID): Reader[SubscriptionRepository[F, SUBSCRIPTION, ID], EitherT[F, SubscriptionNotFound, SUBSCRIPTION_VALUE]]
}
