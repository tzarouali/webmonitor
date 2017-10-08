package webmonitor.repository

import java.util.UUID

import webmonitor.model.Subscription

trait SubscriptionRepository[F[_]] {
  def findSubscriptions(userId: UUID): F[Vector[Subscription]]

  def storeSubscription(subscription: Subscription): F[Unit]
}
