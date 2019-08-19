package com.tookitaki

import java.util.concurrent.{ ExecutorService, Executors }

import cats.effect.Async

package object util {

  def putStrLn[F[_]](v: Any)(implicit F: Async[F]): F[Unit] =
    F.delay(println(v))

  def exec: ExecutorService = Executors.newFixedThreadPool(8)
}
