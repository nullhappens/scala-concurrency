package org.learningconcurrency
package ch3

import java.util.concurrent.LinkedBlockingQueue

object CollectionsIterators extends App {
  val queue = new LinkedBlockingQueue[String]()

  for (i <- 1 to 5500) queue.offer(i.toString)
  execute {
    val it = queue.iterator()
    while(it.hasNext) log(it.next())
  }
  for (i <- 1 to 5500) queue.poll()
  Thread.sleep(1000)
}
