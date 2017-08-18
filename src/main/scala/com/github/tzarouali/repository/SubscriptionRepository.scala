package com.github.tzarouali.repository

import com.github.tzarouali.model.Subscription

trait SubscriptionRepository[F[_]] {
  def findSubscriptions(username: String): F[Vector[Subscription]]
  def storeSubscription(subscription: Subscription): F[Unit]
}
