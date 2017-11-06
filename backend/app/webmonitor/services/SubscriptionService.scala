package webmonitor.services

import cats.data.{EitherT, Kleisli}
import webmonitor.model.SubscriptionNotFound
import webmonitor.repositories.SubscriptionRepository

trait SubscriptionService[F[_], G[_], ID, SUBSCRIPTION, SUBSCRIPTION_VALUE] {

  def findSubscriptions(userId: ID): Kleisli[F, SubscriptionRepository[F, SUBSCRIPTION, ID], Vector[SUBSCRIPTION]]

  def storeSubscription(subscription: SUBSCRIPTION): Kleisli[F, SubscriptionRepository[F, SUBSCRIPTION, ID], Unit]

  def getSubscriptionValue(subscriptionId: ID): Kleisli[G, SubscriptionRepository[F, SUBSCRIPTION, ID], EitherT[F, SubscriptionNotFound, SUBSCRIPTION_VALUE]]
}
