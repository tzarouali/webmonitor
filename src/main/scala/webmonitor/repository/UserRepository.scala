package webmonitor.repository

trait UserRepository[F[_], G[_], USER, ID] {
  def findUser(userId: ID): F[G[Option[USER]]]

  def createUser(user: USER): F[G[ID]]
}
