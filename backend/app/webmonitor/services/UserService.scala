package webmonitor.services

import cats.data.{EitherT, Kleisli}
import webmonitor.model.{LoginError, LogoutError, UserNotFoundError, UserSessionData}
import webmonitor.repositories.UserRepository

trait UserService[F[_], G[_], USER, ID] {

  def login(email: String, password: String): Kleisli[G, UserRepository[F, USER, ID], EitherT[F, LoginError, UserSessionData]]

  def logout(userSessionData: UserSessionData): Kleisli[G, UserRepository[F, USER, ID], EitherT[F, LogoutError, Unit]]

  def tokenValid(userId: ID, token: String): Kleisli[G, UserRepository[F, USER, ID], EitherT[F, UserNotFoundError, Boolean]]

}
