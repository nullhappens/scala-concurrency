package org.learningconcurrency
package ch3

import java.util.concurrent.ForkJoinPool

import scala.concurrent.ExecutionContext


object ExecutionContextCreate extends App {
  val pool = new ForkJoinPool(2)
  val ectx = ExecutionContext.fromExecutorService(pool)
  ectx.execute(new Runnable {
    override def run(): Unit = log("Running on the execution context again")
  })
  Thread.sleep(500)
}


