package webmonitor.services

import cats.data.Kleisli
import webmonitor.repositories.UserRepository

trait UserService[F[_], G[_], USER, ID] {

  def findUser(userId: ID): Kleisli[F, UserRepository[F, G, USER, ID], G[Option[USER]]]

  def createUser(user: USER): Kleisli[F, UserRepository[F, G, USER, ID], G[ID]]

}
