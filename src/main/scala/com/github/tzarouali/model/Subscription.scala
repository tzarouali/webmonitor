package com.github.tzarouali.model

import java.util.UUID

case class Subscription(id: UUID,
                        url: String,
                        jqueryExtractor: String,
                        userId: UUID)
