package webmonitor.repositories.interpreter

import java.util.UUID

import cats.Eval._
import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
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

  override def findUser(email: String, password: String): IO[Option[User]] = {
    IO.fromFuture(always(
      userTable
        .select
        .where(_.email eqs email)
        .and(_.password eqs password)
        .allowFiltering()
        .one()
    ))
  }

  override def updateUserToken(userId: UUID, token: String): IO[Unit] = {
    IO.fromFuture(always(
      userTable
        .update()
        .modify(_.token.setTo(token))
        .future()
        .map(_ => ())
    ))
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

    object token extends StringColumn
  }

  val userTable = new Users
}
