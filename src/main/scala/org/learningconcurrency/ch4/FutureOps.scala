package org.learningconcurrency.ch4

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits._

object FutureOps {
  implicit class FutureOps[T](val self: Future[T]) {
    def or(that: Future[T]): Future[T] = {
      val p = Promise[T]
      self.onComplete(x => p.tryComplete(x))
      that.onComplete(x => p.tryComplete(x))
      p.future
    }
  }
}

