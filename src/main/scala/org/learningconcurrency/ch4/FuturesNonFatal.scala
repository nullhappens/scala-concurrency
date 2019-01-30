package org.learningconcurrency
package ch4

import scala.concurrent._
import ExecutionContext.Implicits.global

object FuturesNonFatal extends App {
  val f = Future {
    throw new InterruptedException
  }
  val g = Future {
    throw new IllegalArgumentException
  }
  f.failed.foreach(t => log(s"error - $t"))
  g.failed.foreach(t => log(s"error - $t"))

}
