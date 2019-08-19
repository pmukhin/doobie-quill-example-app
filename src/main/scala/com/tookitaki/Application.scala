package com.tookitaki

import cats.Applicative
import cats.effect.{ Effect, IO }

import scala.concurrent.Await

trait EnvConfig[F[_]] {
  def getVar(s: String): F[String]
  def getInt(s: String): F[Int]
}

class EnvConfigImpl[F[_]](implicit F: Effect[F]) extends EnvConfig[F] {

  def getVar(s: String): F[String] =
    F.pure(s)

  def getInt(s: String): F[Int] =
    F.map(getVar("343"))(str => str.toInt)
}

object Application extends App {

  val program = for {
    _ <- IO { println("134234") }
    _ <- IO { "pure sdfsdf" }
    _ <- IO { throw new RuntimeException("sdfds") }
    _ <- IO { println("sdfsdf") }
  } yield ()

  import scala.concurrent.duration._

  Await.result(program.unsafeToFuture(), 2.seconds)
}
