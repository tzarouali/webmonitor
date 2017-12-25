package webmonitor.services.interpreter

import java.util.UUID

import cats.data.{EitherT, Reader}
import cats.effect.IO
import webmonitor.model.{SubscriptionFeedValueNotFound, SubscriptionValue}
import webmonitor.repositories.SubscriptionFeedRepository
import webmonitor.services.SubscriptionFeedService

trait SubscriptionFeedServiceInterpreter extends SubscriptionFeedService[IO, UUID, SubscriptionValue] {
  override def getSubscriptionFeedValue(subscriptionId: UUID): Reader[SubscriptionFeedRepository[IO, SubscriptionValue, UUID], EitherT[IO, SubscriptionFeedValueNotFound, SubscriptionValue]] =
    Reader { repo =>
      repo
        .getSubscriptionFeedValue(subscriptionId)
        .toRight(SubscriptionFeedValueNotFound())
    }

  override def storeSubscriptionFeedValue(subscriptionId: UUID, subscriptionValue: SubscriptionValue): Reader[SubscriptionFeedRepository[IO, SubscriptionValue, UUID], IO[Unit]] =
    Reader { repo =>
      repo.storeSubscriptionFeedValue(subscriptionId, subscriptionValue)
    }
}

object SubscriptionFeedServiceInterpreter extends SubscriptionFeedServiceInterpreter
