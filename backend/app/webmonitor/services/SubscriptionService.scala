package webmonitor.services

import cats.data.Kleisli
import webmonitor.repositories.SubscriptionRepository

trait SubscriptionService[F[_], ID, SUBSCRIPTION, SUBSCRIPTION_VALUE] {

  def findSubscriptions(userId: ID): Kleisli[F, SubscriptionRepository[F, SUBSCRIPTION, ID], Vector[SUBSCRIPTION]]

  def storeSubscription(subscription: SUBSCRIPTION): Kleisli[F, SubscriptionRepository[F, SUBSCRIPTION, ID], Unit]

}
