package com.tookitaki.pp2_parallelism

import java.util.concurrent.Executors

import cats.effect.IO
import com.tookitaki.HikariConfig
import com.tookitaki.pp1_basics.DoobieProductRepository
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

object Application extends App {
  implicit val ec = ExecutionContext
    .fromExecutor(Executors.newFixedThreadPool(8))
  implicit val cs = IO.contextShift(ec)

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

  val transactor: HikariTransactor[IO] =
    HikariTransactor.apply[IO](new HikariDataSource(config), ec, ec)

  val productRepository = new DoobieProductRepository[IO](transactor)

  val ioProgram = for {
    prod1Fiber <- productRepository.findById(2).start
    prod2Fiber <- productRepository.findById(3).start
    prod3Fiber <- productRepository.findById(4).start
    prod1      <- prod3Fiber.join
    prod2      <- prod2Fiber.join
    prod3      <- prod1Fiber.join
    _          <- putStrLn((prod1, prod2, prod3))
  } yield ()

  ioProgram.unsafeRunSync()
}
