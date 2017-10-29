package webmonitor.repositories.interpreter

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

import cats.Eval._
import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.jdk8._
import webmonitor.config.ApplicationConfigReader
import webmonitor.model.User
import webmonitor.repositories.UserRepository

trait CassandraUserRepositoryInterpreter extends UserRepository[IO, User, UUID] {

  import CassandraUserRepositoryInterpreter._
  import CassandraUserRepositoryInterpreter.userTable._

  override def findUser(userId: UUID): IO[Option[User]] = {
    IO.fromFuture(always(
      userTable
        .select
        .where(_.id eqs userId)
        .allowFiltering()
        .one()
    ))
  }

  override def findUser(email: String): IO[Option[User]] = {
    IO.fromFuture(always(
      userTable
        .select
        .where(_.email eqs email)
        .allowFiltering()
        .one()
    ))
  }

  override def updateUserToken(userId: UUID, token: String): IO[Unit] = {
    IO.fromFuture(always({
      val theToken = if (token.nonEmpty) Some(token) else None
      val expiration = if (token.nonEmpty) Some(generateExpirationDateTime) else None
      userTable
        .update()
        .where(_.id eqs userId)
        .modify(_.usertoken.setTo(theToken))
        .and(_.tokenexpiration.setTo(expiration))
        .future()
        .map(_ => ())
    }))
  }

  private val generateExpirationDateTime = LocalDateTime.now(ZoneId.of("UTC")).plusHours(1L)

  override def clearUserToken(userId: UUID): IO[Unit] = {
    updateUserToken(userId, "")
  }

}

object CassandraUserRepositoryInterpreter extends CassandraUserRepositoryInterpreter {
  lazy val connector: CassandraConnection = ApplicationConfigReader.config.cassandraKeySpace

  class Users extends Table[Users, User] with connector.Connector {

    object id extends UUIDColumn with PrimaryKey

    object name extends StringColumn

    object email extends StringColumn with PartitionKey

    object password extends StringColumn with PartitionKey

    object secret extends StringColumn

    object usertoken extends OptionalStringColumn

    object tokenexpiration extends OptionalCol[LocalDateTime]
  }

  val userTable = new Users
}
