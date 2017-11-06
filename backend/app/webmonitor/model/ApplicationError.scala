package webmonitor.model

final case class UserNotFoundError()
final case class LoginError(err: String)
final case class LogoutError(err: String)

final case class SubscriptionNotFound() extends Product with Serializable
