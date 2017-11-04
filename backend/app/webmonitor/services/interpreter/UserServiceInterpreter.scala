package webmonitor.services.interpreter

import java.math.BigInteger
import java.security.SecureRandom
import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

import cats.data.Kleisli
import cats.effect.IO
import org.apache.commons.codec.digest.DigestUtils
import webmonitor.model._
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
          val expiration = Some(LocalDateTime.now(defaultTimeZone).plusHours(1L))
          repo.updateUserToken(u.id, token, expiration).flatMap(_ => IO(Right(sessionData)))
        case _ =>
          IO(Left(LoginError("Error trying to login. Verify your credentials.")))
      })
  }

  override def logout(userSessionData: UserSessionData) = Kleisli { repo =>
    repo
      .findUser(userSessionData.userId)
      .flatMap({
        case Some(u) if u.userToken.isDefined && u.userToken.get == userSessionData.token =>
          repo.clearUserToken(u.id).map(_ => Right(()))
        case _ =>
          IO(Left(LogoutError("Error trying to logout.")))
      })
  }

  override def tokenValid(userId: UUID, token: String) = Kleisli { repo =>
    repo
      .findUser(userId)
      .flatMap({
        case Some(u) =>
          (u.userToken, u.tokenExpiration) match {
            case (Some(t), Some(exp)) =>
              IO(Right(t == token && tokenNotExpired(exp)))
            case _ =>
              IO(Right(false))
          }
        case _ =>
          IO(Left(UserNotFoundError()))
      })
  }

}

object UserServiceInterpreter extends UserServiceInterpreter {

  private val defaultTimeZone = ZoneId.of("UTC")

  private val tokenNotExpired: LocalDateTime => Boolean =
    expirationDate => LocalDateTime.now(defaultTimeZone).isBefore(expirationDate)

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
