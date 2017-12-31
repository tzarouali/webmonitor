package model

import java.time.LocalDateTime
import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._
import io.circe.java8.time._

final case class SubscriptionValue(id: UUID,
                                   subscriptionId: UUID,
                                   value: String,
                                   lastUpdated: LocalDateTime)

final case class SubscriptionValueNotAvailable(subscriptionId: UUID,
                                               error: String)

object SubscriptionValue {
  implicit lazy val subscriptionValueEncoder: Encoder[SubscriptionValue] = deriveEncoder
}

object SubscriptionValueNotAvailable {
  implicit lazy val subscriptionValueNotAvailableEncoder: Encoder[SubscriptionValueNotAvailable] = deriveEncoder
}
