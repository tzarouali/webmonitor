package webmonitor.model

import java.util.UUID

case class SubscriptionValue(subscriptionId: UUID,
                             userId: UUID,
                             value: String)
