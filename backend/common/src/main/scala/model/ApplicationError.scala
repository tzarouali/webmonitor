package model

import io.circe._
import io.circe.generic.semiauto._

sealed trait ApplicationError extends Product with Serializable

final case class UserNotFoundError() extends ApplicationError

final case class LoginError(err: String) extends ApplicationError

final case class LogoutError(err: String) extends ApplicationError

final case class SubscriptionNotFound() extends ApplicationError

final case class SubscriptionFeedValueNotFound() extends ApplicationError

final case class SubscriptionFeedValueParsingError(err: String) extends ApplicationError

object ApplicationError {

  implicit lazy val valueParsingErrorEncoder: Encoder[SubscriptionFeedValueParsingError] = deriveEncoder

}
