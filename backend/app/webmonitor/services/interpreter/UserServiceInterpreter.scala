package webmonitor.services.interpreter

import java.math.BigInteger
import java.security.SecureRandom
import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

import cats.Id
import cats.data.{EitherT, Reader}
import cats.effect.IO
import org.apache.commons.codec.digest.DigestUtils
import webmonitor.model._
import webmonitor.services.UserService

trait UserServiceInterpreter extends UserService[IO, User, UUID] {

  import UserServiceInterpreter._

  override def login(email: String, password: String) = Reader { repo =>
    val userSessionData: Id[EitherT[IO, LoginError, UserSessionData]] =
    repo
      .findUser(email)
      .toRight(LoginError("Error trying to login. Verify your credentials."))
      .flatMap {
        case u if passwordsMatch(u, password) =>
          val token = generateToken()
          val sessionData = UserSessionData(u.id, token)
          val expiration = Some(LocalDateTime.now(defaultTimeZone).plus(defaultExpirationTime, defaultExpirationUnit))
          EitherT.right(repo.updateUserToken(u.id, token, expiration).flatMap(_ => IO(sessionData)))
        case _ =>
          EitherT.leftT(LoginError("Error trying to login. Verify your credentials."))
      }
    userSessionData
  }

  override def logout(userSessionData: UserSessionData) = Reader { repo =>
    val logoutResult: Id[EitherT[IO, LogoutError, Unit]] =
      repo
        .findUser(userSessionData.userId)
        .toRight(LogoutError("Error trying to logout."))
        .flatMap {
          case u if u.userToken.get == userSessionData.token =>
            EitherT.right(repo.clearUserToken(u.id).map(_ => ()))
          case _ =>
            EitherT.leftT(LogoutError("Error trying to logout."))
        }
    logoutResult
  }

  override def tokenValid(userId: UUID, token: String) = Reader { repo =>
    val tokenValid: Id[EitherT[IO, UserNotFoundError, Boolean]] =
      repo
        .findUser(userId)
        .toRight(UserNotFoundError())
        .flatMap { u =>
          (u.userToken, u.tokenExpiration) match {
            case (Some(t), Some(exp)) =>
              EitherT.rightT(t == token && tokenNotExpired(exp))
            case _ =>
              EitherT.rightT(false)
          }
        }
    tokenValid
  }

}

object UserServiceInterpreter extends UserServiceInterpreter {

  private val defaultExpirationTime = 1L
  private val defaultExpirationUnit = ChronoUnit.HOURS

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
