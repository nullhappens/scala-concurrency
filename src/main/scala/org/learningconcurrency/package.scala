package org

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

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

  def handleMessage(t: Try[String]): Unit = t match {
    case Failure(exception) => log(s"unexpected failure - $exception")
    case Success(value) => log(value)
  }

}
