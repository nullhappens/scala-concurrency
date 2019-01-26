package org.learningconcurrency
package ch3

import java.util.concurrent.ForkJoinPool

object ExecutorsCreate extends App {
  val executor = new ForkJoinPool()
  executor.execute(new Runnable {
    override def run(): Unit = log("this task is run asynchronously")
  })

  Thread.sleep(500)
}
