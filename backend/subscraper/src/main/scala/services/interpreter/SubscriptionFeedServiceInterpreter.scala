package services.interpreter

import java.util.UUID

import cats.data.Reader
import cats.effect.IO
import model.SubscriptionValue
import repositories.SubscriptionFeedRepository
import services.SubscriptionFeedService

trait SubscriptionFeedServiceInterpreter extends SubscriptionFeedService[IO, UUID, SubscriptionValue] {

  override def storeSubscriptionFeedValue(subscriptionId: UUID, subscriptionValue: SubscriptionValue): Reader[SubscriptionFeedRepository[IO, SubscriptionValue, UUID], IO[Unit]] =
    Reader { repo =>
      repo.storeSubscriptionFeedValue(subscriptionId, subscriptionValue)
    }

}

object SubscriptionFeedServiceInterpreter extends SubscriptionFeedServiceInterpreter
