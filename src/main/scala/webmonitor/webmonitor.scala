package webmonitor

import cats.data.Kleisli
import cats.effect.IO

import scala.concurrent.Future

package object webmonitor {
  type RepoResult[A] = Kleisli[IO, Unit, Future[A]]
}
