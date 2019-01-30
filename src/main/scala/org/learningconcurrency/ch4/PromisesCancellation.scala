package org.learningconcurrency
package ch4

import scala.concurrent.{CancellationException, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

object PromisesCancellation extends App {

  type Cancellable[T] = (Promise[Unit], Future[T])

  def cancellable[T](b: Future[Unit] => T): Cancellable[T] = {
    val cancel = Promise[Unit]
    val f = Future {
      val r = b(cancel.future)
      if (!cancel.tryFailure(new Exception))
        throw new CancellationException
      r
    }
    (cancel, f)
  }

  val (cancel, value) = cancellable { cancel =>
    var i = 0
    while (i < 5) {
      if (cancel.isCompleted) throw new CancellationException
      Thread.sleep(500)
      log(s"$i: working")
      i += 1
    }
    "resulting value"
  }

  Thread.sleep(1500)
  cancel.trySuccess()
  log("computation cancelled!")
  Thread.sleep(2000)
}
