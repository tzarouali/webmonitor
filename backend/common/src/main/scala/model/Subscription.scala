package model

import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._

final case class Subscription(id: UUID,
                        name: String,
                        userId: UUID,
                        url: String,
                        cssSelector: String,
                        useHtmlExtractor: Boolean)

object Subscription {
  implicit val subscriptionEncoder: Encoder[Subscription] = deriveEncoder
}

final case class NewSubscriptionReq(url: String,
                              cssSelector: String,
                              name: String,
                              useHtmlExtractor: Boolean)

object NewSubscriptionReq {
  implicit val newSubscriptionDecoder: Decoder[NewSubscriptionReq] = deriveDecoder
}
