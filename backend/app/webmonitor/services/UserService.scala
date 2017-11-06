package webmonitor.services

import cats.data.{EitherT, Reader}
import webmonitor.model.{LoginError, LogoutError, UserNotFoundError, UserSessionData}
import webmonitor.repositories.UserRepository

trait UserService[F[_], USER, ID] {

  def login(email: String, password: String): Reader[UserRepository[F, USER, ID], EitherT[F, LoginError, UserSessionData]]

  def logout(userSessionData: UserSessionData): Reader[UserRepository[F, USER, ID], EitherT[F, LogoutError, Unit]]

  def tokenValid(userId: ID, token: String): Reader[UserRepository[F, USER, ID], EitherT[F, UserNotFoundError, Boolean]]

}
