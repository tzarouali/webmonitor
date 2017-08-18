package com.github

import cats.data.Kleisli
import cats.effect.IO

import scala.concurrent.Future

package object tzarouali {
  type RepoResult[A] = Kleisli[IO, Unit, Future[A]]
}
