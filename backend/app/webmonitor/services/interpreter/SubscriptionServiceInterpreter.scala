package webmonitor.services.interpreter

import java.util.UUID

import cats.data.Kleisli
import cats.effect.IO
import webmonitor.model.{Subscription, SubscriptionValue}
import webmonitor.services.SubscriptionService

trait SubscriptionServiceInterpreter extends SubscriptionService[IO, UUID, Subscription, SubscriptionValue] {
  override def findSubscriptions(userId: UUID) = Kleisli { repo =>
    repo.findSubscriptions(userId)
  }

  override def storeSubscription(subscription: Subscription) = Kleisli { repo =>
    repo.storeSubscription(subscription)
  }

}

object SubscriptionServiceInterpreter extends SubscriptionServiceInterpreter
