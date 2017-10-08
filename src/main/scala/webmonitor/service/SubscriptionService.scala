package webmonitor.service

import cats.data.Kleisli
import webmonitor.repository.SubscriptionRepository

import scala.language.higherKinds

trait SubscriptionService[F[_], G[_], SUBSCRIPTION, ID] {

  def findSubscriptions(userId: ID): Kleisli[F, SubscriptionRepository[F, G, SUBSCRIPTION], G[Vector[SUBSCRIPTION]]]

  def storeSubscription(subscription: SUBSCRIPTION): Kleisli[F, SubscriptionRepository[F, G, SUBSCRIPTION], G[Unit]]

}
