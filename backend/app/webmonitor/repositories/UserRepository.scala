package webmonitor.repositories

trait UserRepository[F[_], USER, ID] {

  def findUser(userId: ID): F[Option[USER]]

  def findUser(email: String, password: String): F[Option[USER]]

  def updateUserToken(userId: ID, token: String): F[Unit]
}
