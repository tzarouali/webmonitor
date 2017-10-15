package webmonitor.services.interpreter

import java.math.BigInteger
import java.security.SecureRandom
import java.util.UUID

import cats.data.Kleisli
import cats.effect.IO
import org.apache.commons.codec.digest.DigestUtils
import webmonitor.model.{LoginError, LogoutError, User, UserSessionData}
import webmonitor.services.UserService

trait UserServiceInterpreter extends UserService[IO, User, UUID] {

  import UserServiceInterpreter._

  override def login(email: String, password: String) = Kleisli { repo =>
    repo
      .findUser(email)
      .flatMap({
        case Some(u) if passwordsMatch(u, password) =>
          val token = generateToken()
          val sessionData = UserSessionData(u.id, token)
          repo.updateUserToken(u.id, token).flatMap(_ => IO(Right(sessionData)))
        case _ =>
          IO(Left(LoginError("Error trying to login. Verify your credentials.")))
      })
  }

  override def logout(userSessionData: UserSessionData) = Kleisli { repo =>
    repo
      .findUser(userSessionData.userId)
      .flatMap({
        case Some(u) if u.usertoken.isDefined && u.usertoken.get == userSessionData.token =>
          repo.updateUserToken(u.id, "").map(_ => Right(()))
        case _ =>
          IO(Left(LogoutError("Error trying to logout.")))
      })
  }

}

object UserServiceInterpreter extends UserServiceInterpreter {

  private def generateToken(): String = {
    val random = new SecureRandom()
    new BigInteger(130, random).toString(32)
  }

  private def generateChecksum(str: String): String = {
    DigestUtils.sha256Hex(str)
  }

  private def passwordsMatch(user: User, password: String): Boolean = {
    generateChecksum(user.secret + password) == user.password
  }

}
