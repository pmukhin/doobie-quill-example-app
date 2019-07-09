package com.tookitaki.pp1_basics

import cats.effect.Async
import doobie.Transactor
import shapeless._

case class ProductShort(id: Long, title: String)
case class Product(id: Long, title: String, price: Double, categoryId: Long, color: Option[String])

trait ProductRepository[F[_]] {
  def findById(id: Long): F[Option[Product]]
}

class DoobieProductRepository[F[_]](xa: Transactor[F])(implicit
                                                       F: Async[F])
    extends ProductRepository[F] {

  import doobie.implicits._

  override def findById(id: Long): F[Option[Product]] =
    sql"SELECT * FROM Products WHERE id = $id"
      .query[Product]
      .option
      .transact[F](xa)

}
