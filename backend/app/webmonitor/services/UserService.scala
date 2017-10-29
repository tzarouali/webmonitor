package webmonitor.services

import cats.data.Kleisli
import webmonitor.model.{LoginError, LogoutError, UserSessionData, UserNotFoundError}
import webmonitor.repositories.UserRepository

trait UserService[F[_], USER, ID] {

  def login(email: String, password: String): Kleisli[F, UserRepository[F, USER, ID], Either[LoginError, UserSessionData]]

  def logout(userSessionData: UserSessionData): Kleisli[F, UserRepository[F, USER, ID], Either[LogoutError, Unit]]

  def tokenValid(userId: ID, token: String): Kleisli[F, UserRepository[F, USER, ID], Either[UserNotFoundError, Boolean]]

}
