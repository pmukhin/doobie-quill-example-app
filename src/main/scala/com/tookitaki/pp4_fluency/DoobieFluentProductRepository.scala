package com.tookitaki.pp4_fluency

import cats.Applicative
import cats.effect.Async
import cats.syntax.all._
import com.tookitaki.pp1_basics.{ Product, ProductRepository }
import doobie.util.transactor.Transactor

class DoobieFluentProductRepository[F[_]](xa: Transactor[F])(
  implicit
  F: Async[F]
) extends ProductRepository[F] {

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
}
