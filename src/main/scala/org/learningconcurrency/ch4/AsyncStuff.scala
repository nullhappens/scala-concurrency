package org.learningconcurrency
package ch4

import scala.concurrent.Future
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext.Implicits._
import scala.async.Async._

object AsyncStuff extends App {

  def delay(n: Int): Future[Unit] = async {
    blocking {
      Thread.sleep(n * 1000)
    }
  }
  def countdown(n: Int)(f: Int => Unit): Future[Unit] = async {
    var i = n
    while (i > 0) {
      f(i)
      await(delay(1))
      i -= 1
    }
  }

  countdown(10){ n =>
    log(s"T-minus $n seconds")
  } foreach(_ => log(s"BOOM!"))
  Thread.sleep(11000)

}
