package org.learningconcurrency
package ch4

import scala.concurrent.{Await, Future, blocking}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object BlockingFuturesAwait extends App {
  val startTime = System.nanoTime()
  val futures = for (_ <- 0 until 16 ) yield Future { Thread.sleep(1000)}
  for (f <- futures) Await.ready(f, Duration.Inf)
  val endTime = System.nanoTime()
  log(s"Total time = ${(endTime - startTime) / 1000000} ms")
  log(s"Total CPUs = ${Runtime.getRuntime.availableProcessors()}")
}

object BlockingFuturesAwait2 extends App {
  val startTime = System.nanoTime()
  val futures = for (_ <- 0 until 16 ) yield Future {
    blocking {
      Thread.sleep(1000)
    }
  }
  for (f <- futures) Await.ready(f, Duration.Inf)
  val endTime = System.nanoTime()
  log(s"Total time = ${(endTime - startTime) / 1000000} ms")
  log(s"Total CPUs = ${Runtime.getRuntime.availableProcessors()}")
}
