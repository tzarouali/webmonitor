package webmonitor.services.interpreter

import java.util.UUID

import cats.Id
import cats.data.{EitherT, Kleisli}
import cats.effect.IO
import org.jsoup.Jsoup
import webmonitor.model.{Subscription, SubscriptionNotFound, SubscriptionValue}
import webmonitor.services.SubscriptionService

trait SubscriptionServiceInterpreter extends SubscriptionService[IO, Id, UUID, Subscription, SubscriptionValue] {
  override def findSubscriptions(userId: UUID) = Kleisli { repo =>
    repo.findSubscriptions(userId)
  }

  override def storeSubscription(subscription: Subscription) = Kleisli { repo =>
    repo.storeSubscription(subscription)
  }

  override def getSubscriptionValue(subscriptionId: UUID) = Kleisli { repo =>
    val subscriptionValue: Id[EitherT[IO, SubscriptionNotFound, SubscriptionValue]] = repo
      .getSubscription(subscriptionId)
      .toRight(SubscriptionNotFound())
      .map(sub => {
        val doc = Jsoup.connect(sub.url).ignoreContentType(true).ignoreHttpErrors(true).get()
        val value = doc.select(sub.jqueryExtractor).html()
        SubscriptionValue(sub.id, value)
      })
    subscriptionValue
  }
}

object SubscriptionServiceInterpreter extends SubscriptionServiceInterpreter
