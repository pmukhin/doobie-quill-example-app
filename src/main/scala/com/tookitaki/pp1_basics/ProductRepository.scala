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

  val t = Long :: String :: HNil

  override def findById(id: Long): F[Option[Product]] = {
    println(s"from fiber: ${scala.util.Random.nextLong()}")
    sql"SELECT * FROM Products WHERE id = $id"
      .query[Product]
      .option
      .transact[F](xa)
  }

  def findByIdShort(id: Long): F[Option[t.type]] =
    sql"SELECT id, title FROM Products WHERE id = $id"
      .query[t.type]
      .option
      .transact[F](xa)

  val res = F.map(findByIdShort(5)) { o =>
    o.map { p =>
      Generic[ProductShort].to(p)
    }
  }

}
