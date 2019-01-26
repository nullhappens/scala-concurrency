package org.learningconcurrency.ch2

import org.learningconcurrency.{log, thread}

object ThreadCreation extends App{

  val t = thread {
    Thread.sleep(1000)
    log("new thread running.")
    Thread.sleep(1000)
    log("still running")
    Thread.sleep(1000)
    log("Completed.")
  }
  t.join()
  log("New thread joined")

}

object ThreadsUnprotectedUid extends App {
  var uidCount = 0L

  // synchronized guarantees getUniqueId is atomic
  // always make sure to explicitly state the receiver of synchronized
  def getUniqueId(): Long = this.synchronized {
    val freshUid = uidCount + 1
    uidCount = freshUid
    freshUid
  }

  def printUniqueUIds(n: Int): Unit = {
    val uids = for (i <- 0 until n) yield getUniqueId()
    log (s"Generated uids: $uids")
  }

  val t = thread {
    printUniqueUIds(5)
  }
  printUniqueUIds(5)
  t.join()
}
