package webmonitor.service

import cats.data.Kleisli
import webmonitor.repository.SubscriptionRepository

trait SubscriptionService[F[_], G[_], SUBSCRIPTION, ID] {

  def findSubscriptions(userId: ID): Kleisli[F, SubscriptionRepository[F, G, SUBSCRIPTION, ID], G[Vector[SUBSCRIPTION]]]

  def storeSubscription(subscription: SUBSCRIPTION): Kleisli[F, SubscriptionRepository[F, G, SUBSCRIPTION, ID], G[Unit]]

}
