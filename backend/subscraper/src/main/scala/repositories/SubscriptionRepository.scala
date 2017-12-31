package repositories

trait SubscriptionRepository[F[_], SUBSCRIPTION] {

  def findAllSubscriptions(): F[Vector[SUBSCRIPTION]]

}
