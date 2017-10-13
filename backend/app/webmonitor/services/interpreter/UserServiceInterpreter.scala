package webmonitor.services.interpreter

import java.util.UUID

import cats.data.Kleisli
import cats.effect.IO
import webmonitor.model.User
import webmonitor.services.UserService

import scala.concurrent.Future

trait UserServiceInterpreter extends UserService[IO, Future, User, UUID] {
  override def findUser(userId: UUID) = Kleisli { repo =>
    repo.findUser(userId)
  }

  override def createUser(user: User) = Kleisli { repo =>
    repo.createUser(user)
  }
}

object UserServiceInterpreter extends UserServiceInterpreter
