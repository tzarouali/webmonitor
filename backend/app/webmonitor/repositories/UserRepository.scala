package webmonitor.repositories

import java.time.LocalDateTime

import cats.data.OptionT

trait UserRepository[F[_], USER, ID] {

  def findUser(userId: ID): OptionT[F, USER]

  def findUser(email: String): OptionT[F, USER]

  def updateUserToken(userId: ID, token: String, tokenExpiration: Option[LocalDateTime]): F[Unit]

  def clearUserToken(userId: ID): F[Unit]
}
