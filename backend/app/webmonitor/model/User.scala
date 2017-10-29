package webmonitor.model

import java.time.LocalDateTime
import java.util.UUID

import io.circe._
import io.circe.generic.semiauto._
import io.circe.java8.time._

final case class User(id: UUID,
                      name: String,
                      email: String,
                      password: String,
                      secret: String,
                      userToken: Option[String],
                      tokenExpiration: Option[LocalDateTime])
object User {
  implicit lazy val userEncoder: Encoder[User] = deriveEncoder
}

final case class UserSessionData(userId: UUID, token: String)

object UserSessionData {
  implicit val sessionDataEncoder: Encoder[UserSessionData] = deriveEncoder
}

final case class UserLogin(email: String, password: String)

object UserLogin {
  implicit val userLoginDataDecoder: Decoder[UserLogin] = deriveDecoder
}
