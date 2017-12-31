package services

import cats.data.Reader
import repositories.SubscriptionRepository

trait SubscriptionService[F[_], ID, SUBSCRIPTION, SUBSCRIPTION_VALUE] {

  def findAllSubscriptions(): Reader[SubscriptionRepository[F, SUBSCRIPTION, ID], F[Vector[SUBSCRIPTION]]]

  def findSubscriptions(userId: ID): Reader[SubscriptionRepository[F, SUBSCRIPTION, ID], F[Vector[SUBSCRIPTION]]]

  def storeSubscription(subscription: SUBSCRIPTION): Reader[SubscriptionRepository[F, SUBSCRIPTION, ID], F[Unit]]

}
