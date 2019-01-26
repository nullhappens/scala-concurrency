package org.learningconcurrency
package ch3

import java.util.concurrent.atomic._

import scala.annotation.tailrec

object AtomicUid extends App {
  private val uid = new AtomicLong(0L)

  // def getUniqueId: Long = uid.incrementAndGet()
  @tailrec
  def getUniqueId: Long = {
    val oldUid = uid.get()
    val newUid = oldUid + 1
    if (uid.compareAndSet(oldUid, newUid)) newUid
    else getUniqueId
  }

  execute{
    log(s"Uid asynchronously $getUniqueId")
  }
  log(s"Got a unique id: $getUniqueId")

  Thread.sleep(500)
}
