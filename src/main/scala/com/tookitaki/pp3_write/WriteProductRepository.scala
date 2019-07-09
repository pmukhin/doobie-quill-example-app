package com.tookitaki.pp3_write

import com.tookitaki.pp1_basics.Product
import cats.effect.Async
import cats.syntax.all._
import doobie.util.transactor.Transactor

trait WriteProductRepository[F[_]] {
  def update(p: Product): F[Either[String, Unit]]
}

class DoobieWriteProductRepository[F[_]](xa: Transactor[F])(implicit
                                                            F: Async[F])
    extends WriteProductRepository[F] {

  import doobie.implicits._

  override def update(p: Product): F[Either[String, Unit]] =
    sql"""UPDATE Products
         |SET 
         |  title = ${p.title}, 
         |  price = ${p.price}, 
         |  categoryId = ${p.categoryId},
         |  color = ${p.color}
         | WHERE id = ${p.id}
         |  """.stripMargin.update.run
      .transact(xa)
      .map { i =>
        if (i == 1) Right() else Left("record is not updated")
      }
}
