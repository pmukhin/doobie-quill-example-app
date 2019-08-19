package com.tookitaki.pp4_fluency

import cats.effect.{ Async, Bracket }
import cats.syntax.all._
import com.tookitaki.pp1_basics.{ Product, ProductRepository }
import com.tookitaki.pp3_write.WriteProductRepository
import doobie.util.transactor.Transactor

class DoobieFluentProductRepository[F[_]](xa: Transactor[F])(
  implicit
  F: Async[F],
  ev: Bracket[F, Throwable]
) extends ProductRepository[F]
    with WriteProductRepository[F] {

  import doobie.implicits._
  import io.getquill._

  val dc = new doobie.quill.DoobieContext.MySQL(Literal)
  import dc._

  implicit val productSchemaMeta =
    schemaMeta[Product]("Products")

  private def makeQ(id: Long) =
    quote {
      query[Product]
        .filter(_.id == lift(id))
    }

  override def findById(id: Long): F[Option[Product]] =
    run(makeQ(id)).transact[F](xa).map(_.headOption)

  override def update(p: Product): F[Either[String, Unit]] =
    run(query[Product].filter(_.id == lift(p.id)).update(lift(p)))
      .transact[F](xa)
      .map { r =>
        if (r == 1) ().asRight else "0 rows updated".asLeft
      }
}
