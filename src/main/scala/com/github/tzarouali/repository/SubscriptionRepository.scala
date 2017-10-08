package com.github.tzarouali.repository

import java.util.UUID

import com.github.tzarouali.model.Subscription

trait SubscriptionRepository[F[_]] {
  def findSubscriptions(userId: UUID): F[Vector[Subscription]]

  def storeSubscription(subscription: Subscription): F[Unit]
}
