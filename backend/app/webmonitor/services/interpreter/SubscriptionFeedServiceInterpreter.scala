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
}

object SubscriptionFeedServiceInterpreter extends SubscriptionFeedServiceInterpreter
