package webmonitor.services.interpreter

import java.util.UUID

import cats.data.Reader
import cats.effect.IO
import webmonitor.model.{Subscription, SubscriptionValue}
import webmonitor.services.SubscriptionService

trait SubscriptionServiceInterpreter extends SubscriptionService[IO, UUID, Subscription, SubscriptionValue] {

  override def findAllSubscriptions() = Reader { repo =>
    repo.findAllSubscriptions()
  }

  override def findSubscriptions(userId: UUID) = Reader { repo =>
    repo.findSubscriptions(userId)
  }

  override def storeSubscription(subscription: Subscription) = Reader { repo =>
    repo.storeSubscription(subscription)
  }

}

object SubscriptionServiceInterpreter extends SubscriptionServiceInterpreter
