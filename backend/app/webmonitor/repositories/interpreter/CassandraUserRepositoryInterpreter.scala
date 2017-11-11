package webmonitor.repositories.interpreter

import java.time.LocalDateTime
import java.util.UUID

import cats.Eval._
import cats.data.OptionT
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

  override def findUser(userId: UUID): OptionT[IO, User] = {
    OptionT(
      IO.fromFuture(always(
        userTable
          .select
          .where(_.id eqs userId)
          .allowFiltering()
          .one()
      ))
    )
  }

  override def findUser(email: String): OptionT[IO, User] = {
    OptionT(
      IO.fromFuture(always(
        userTable
          .select
          .where(_.email eqs email)
          .allowFiltering()
          .one()
      ))
    )
  }

  override def updateUserToken(userId: UUID, token: String, tokenExpiration: Option[LocalDateTime]): IO[Unit] = {
    IO.fromFuture(always({
      val theToken = if (token.nonEmpty) Some(token) else None
      userTable
        .update()
        .where(_.id eqs userId)
        .modify(_.userToken.setTo(theToken))
        .and(_.tokenexpiration.setTo(tokenExpiration))
        .future()
        .map(_ => ())
    }))
  }

  override def clearUserToken(userId: UUID): IO[Unit] = {
    updateUserToken(userId, "", None)
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

    object userToken extends OptionalStringColumn {
      override def name: String = "user_token"
    }

    object tokenexpiration extends OptionalCol[LocalDateTime] {
      override def name: String = "token_expiration"
    }

  }

  val userTable = new Users
}
