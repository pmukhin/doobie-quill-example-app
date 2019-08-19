package com.tookitaki.pp2_parallelism

import java.util.concurrent.Executors

import cats.effect.IO
import com.tookitaki.HikariConfig
import com.tookitaki.pp4_fluency.DoobieFluentProductRepository
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object Application extends App {
  implicit val ec = ExecutionContext
    .fromExecutor(Executors.newFixedThreadPool(8))
  implicit val cs = IO.contextShift(ec)

  val config = HikariConfig(
    "jdbc:mysql://127.0.0.1:3306/doobie-pp",
    "doobie",
    "helloworld",
    4
  )

  import cats.implicits._

  val xa: HikariTransactor[IO] =
    HikariTransactor.apply[IO](new HikariDataSource(config), ec, ec)

  val productRepository = new DoobieFluentProductRepository[IO](xa)

  val putStrLn = com.tookitaki.util.putStrLn[IO] _

  val fetchProduct = productRepository.findById _

  val ioProgram =
    fetchProduct(2).start.bracket(
      prod1Fiber =>
        fetchProduct(3).start.bracket(
          prod2Fiber =>
            fetchProduct(4).start.bracket(
              prod3Fiber => (prod1Fiber.join, prod2Fiber.join, prod3Fiber.join).tupled
            )(_.cancel)
        )(_.cancel)
    )(_.cancel)

  ioProgram.flatMap(putStrLn).unsafeRunSync()
}
