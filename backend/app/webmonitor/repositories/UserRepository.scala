package webmonitor.repositories

import java.time.LocalDateTime

trait UserRepository[F[_], USER, ID] {

  def findUser(userId: ID): F[Option[USER]]

  def findUser(email: String): F[Option[USER]]

  def updateUserToken(userId: ID, token: String, tokenExpiration: Option[LocalDateTime]): F[Unit]

  def clearUserToken(userId: ID): F[Unit]
}
