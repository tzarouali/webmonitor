package webmonitor.model

import java.util.UUID

case class SubscriptionValue(subscriptionId: UUID,
                             value: String) extends Product with Serializable
