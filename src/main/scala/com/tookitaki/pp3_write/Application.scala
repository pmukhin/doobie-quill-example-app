package com.tookitaki.pp3_write

import java.util.concurrent.Executors

import cats.data.{ EitherT, OptionT }
import cats.effect.IO
import cats.syntax.all._
import com.tookitaki.HikariConfig
import com.tookitaki.pp1_basics.DoobieProductRepository
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

object Application extends App {
  implicit val ec = Executors.newFixedThreadPool(8)
  implicit val cs = IO.contextShift(ExecutionContext.global)

  def putStrLn(v: Any)          = IO.pure(println(v.toString))
  def info(m: String): IO[Unit] = IO.pure(println(m))

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

  val productRepository      = new DoobieProductRepository[IO](transactor)
  val writeProductRepository = new DoobieWriteProductRepository[IO](transactor)

  val id = 6

  val ioProgram = for {
    // retrieve record from db
    productEithTr <- EitherT
                      .fromOptionF(
                        productRepository.findById(id),
                        s"product #$id does not exist"
                      )

    _ = info(s"recieved product with id of ${productEithTr.id}")

    // update according to the logic
    updatedProd = productEithTr
      .copy(title = "New awesome title here")

    // update in the db
    eff <- EitherT(
            writeProductRepository
              .update(updatedProd)
          )
  } yield eff // voila

  ioProgram.value
    .unsafeRunSync()
    .fold(
      e => println(s"error: $e"),
      _ => println("successfully updated product")
    )
}
