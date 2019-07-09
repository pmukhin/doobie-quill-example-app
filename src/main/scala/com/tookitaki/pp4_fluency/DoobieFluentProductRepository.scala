package com.tookitaki.pp4_fluency

import cats.Applicative
import cats.effect.Async
import com.tookitaki.pp1_basics.Product
import com.tookitaki.pp1_basics.ProductRepository
import doobie.util.transactor.Transactor

class DoobieFluentProductRepository[F[_]: Applicative](xa: Transactor[F])(
  implicit
  F: Async[F]
) extends ProductRepository[F] {

  import doobie.implicits._
  import io.getquill._
  import doobie.quill.DoobieContext

  val dc = new DoobieContext.MySQL(Literal)
  import dc._

  private def makeQ(id: Long) =
    quote {
      query[Product]
        .filter(_.id == id)
        .distinct
    }

  override def findById(id: Long): F[Option[Product]] = ???
}
