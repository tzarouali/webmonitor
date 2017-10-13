package webmonitor.services.interpreter

import java.util.UUID

import cats.data.Kleisli
import cats.effect.IO
import webmonitor.model.Subscription
import webmonitor.services.SubscriptionService

import scala.concurrent.Future

trait SubscriptionServiceInterpreter extends SubscriptionService[IO, Future, Subscription, UUID] {
  override def findSubscriptions(userId: UUID) = Kleisli { repo =>
    repo.findSubscriptions(userId)
  }

  override def storeSubscription(subscription: Subscription) = Kleisli { repo =>
    repo.storeSubscription(subscription)
  }
}

object SubscriptionServiceInterpreter extends SubscriptionServiceInterpreter
