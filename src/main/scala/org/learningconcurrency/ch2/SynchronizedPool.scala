package org.learningconcurrency.ch2

import org.learningconcurrency.log

object SynchronizedPool extends App {
  import scala.collection._
  private val tasks = mutable.Queue[() => Unit]()

  object Worker extends Thread {
    setDaemon(true)
    setName("Worker")
    def poll() = tasks.synchronized{
      while (tasks.isEmpty) tasks.wait()
      tasks.dequeue()
    }

    override def run() =
      while(true) {
        val task = poll()
        task()
      }
  }

  Worker.start()

  def asynchronous(body: => Unit) = tasks.synchronized {
    tasks.enqueue(() => body)
    tasks.notify()
  }

  asynchronous(log("Hello"))
  asynchronous(log("World!"))
  Thread.sleep(500)
}
