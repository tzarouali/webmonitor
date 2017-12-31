package services

import cats.data.Reader
import repositories.SubscriptionRepository

trait SubscriptionService[F[_], SUBSCRIPTION, SUBSCRIPTION_VALUE] {

  def findAllSubscriptions(): Reader[SubscriptionRepository[F, SUBSCRIPTION], F[Vector[SUBSCRIPTION]]]

}
