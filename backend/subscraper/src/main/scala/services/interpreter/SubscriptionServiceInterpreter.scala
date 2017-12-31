package services.interpreter

import cats.data.Reader
import cats.effect.IO
import model.{Subscription, SubscriptionValue}
import services.SubscriptionService

trait SubscriptionServiceInterpreter extends SubscriptionService[IO, Subscription, SubscriptionValue] {

  override def findAllSubscriptions() = Reader { repo =>
    repo.findAllSubscriptions()
  }

}

object SubscriptionServiceInterpreter extends SubscriptionServiceInterpreter
