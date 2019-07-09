package com.tookitaki.pp1_basics

import java.util.concurrent.Executors

import cats.effect.IO
import com.tookitaki.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

object Application extends App {
  implicit val ec = Executors.newFixedThreadPool(8)
  implicit val cs = IO.contextShift(ExecutionContext.global)

  def putStrLn(v: Any) = IO.pure(println(v.toString))

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:world",
    "postgres",
    ""
  )

  val config = HikariConfig(
    "jdbc:mysql://127.0.0.1:3306/doobie-pp",
    "doobie",
    "helloworld",
    4
  )

  val executionContext = ExecutionContext.fromExecutor(ec)

  val transactor: HikariTransactor[IO] =
    HikariTransactor.apply[IO](
      new HikariDataSource(config),
      executionContext,
      executionContext
    )

  val productRepository = new DoobieProductRepository[IO](transactor)

  val ioProgram = for {
    prod <- productRepository.findById(2)
    _    <- putStrLn(prod)
  } yield ()

  ioProgram.unsafeRunSync()
}
