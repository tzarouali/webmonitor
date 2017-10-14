package webmonitor.model

import java.util.UUID

final case class User(id: UUID, name: String, email: String, password: String, secret: String, token: String)

final case class UserSessionData(userId: UUID, token: String)
