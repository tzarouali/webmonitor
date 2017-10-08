package webmonitor.repository.interpreter

import cats.effect.IO
import com.outworkers.phantom.Table
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._
import webmonitor.config.ApplicationConfigReader
import webmonitor.model.User
import webmonitor.repository.UserRepository

import scala.concurrent.Future

trait CassandraUserRepositoryInterpreter extends UserRepository[IO, Future, User, UUID] {

  import CassandraUserRepositoryInterpreter._
  import CassandraUserRepositoryInterpreter.userTable._

  override def findUser(userId: UUID) = IO {
    userTable
      .select
      .where(_.id eqs userId)
      .allowFiltering()
      .one()
  }

  override def createUser(user: User) = IO {
    userTable
      .insert
      .value(_.id, user.id)
      .value(_.name, user.name)
      .value(_.email, user.email)
      .future()
      .map(user => user.one().getUUID(0))
  }

}

object CassandraUserRepositoryInterpreter extends CassandraUserRepositoryInterpreter {
  lazy val connector: CassandraConnection = ApplicationConfigReader.config.cassandraKeySpace

  class Users extends Table[Users, User] with connector.Connector {

    object id extends UUIDColumn with PartitionKey

    object name extends StringColumn with ClusteringOrder

    object email extends StringColumn

  }

  val userTable = new Users
}
