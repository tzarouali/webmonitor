package webmonitor.model

import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._

case class Subscription(id: UUID,
                        url: String,
                        jqueryExtractor: String,
                        userId: UUID)

object Subscription {
  implicit val subscriptionEncoder: Encoder[Subscription] = deriveEncoder
}

case class NewSubscriptionReq(url: String, jqueryExtractor: String)

object NewSubscriptionReq {
  implicit val newSubscriptionDecoder: Decoder[NewSubscriptionReq] = deriveDecoder
}
