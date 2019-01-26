package org

import scala.concurrent.ExecutionContext

package object learningconcurrency {

  def log(m: String): Unit = println(s"[${Thread.currentThread().getName}]: $m")

  def thread(body: => Unit): Thread = {
    val t = new Thread{
      override def run(): Unit = body
    }
    t.start()
    t
  }

  def execute(body: => Unit): Unit = ExecutionContext.global.execute(new Runnable {
    override def run(): Unit = body
  })


}
