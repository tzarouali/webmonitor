package webmonitor.services.interpreter

import java.util.UUID

import cats.data.Kleisli
import cats.effect.IO
import webmonitor.model.Subscription
import webmonitor.services.SubscriptionService

trait SubscriptionServiceInterpreter extends SubscriptionService[IO, Subscription, UUID] {
  override def findSubscriptions(userId: UUID) = Kleisli { repo =>
    repo.findSubscriptions(userId)
  }

  override def storeSubscription(subscription: Subscription) = Kleisli { repo =>
    repo.storeSubscription(subscription)
  }
}

object SubscriptionServiceInterpreter extends SubscriptionServiceInterpreter
