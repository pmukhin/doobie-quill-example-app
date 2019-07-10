package com.tookitaki.pp3_write

import cats.data.EitherT
import cats.effect.IO
import com.tookitaki.HikariConfig
import com.tookitaki.pp1_basics.DoobieProductRepository
import com.tookitaki.util._
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object Application extends App {
  implicit val ec = ExecutionContext.fromExecutor(exec)
  implicit val cs = IO.contextShift(ec)

  def info(m: String): IO[Unit] = IO.pure(println(m))

  val config = HikariConfig(
    "jdbc:mysql://127.0.0.1:3306/doobie-pp",
    "doobie",
    "helloworld",
    4
  )

  val xa: HikariTransactor[IO] =
    HikariTransactor.apply[IO](new HikariDataSource(config), ec, ec)

  val productRepository      = new DoobieProductRepository[IO](xa)
  val writeProductRepository = new DoobieWriteProductRepository[IO](xa)

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

  val putStrLn = com.tookitaki.util.putStrLn[IO]

  ioProgram.value
    .flatMap {
      _.fold(e => putStrLn(s"error: $e"), _ => putStrLn("successfully updated product"))
    }
    .unsafeRunSync()
}
