package org.learningconcurrency
package ch4

import scala.util.Try

object FuturesTry extends App {
  val threadName: Try[String] = Try(Thread.currentThread.getName)
  val someText: Try[String] = Try("Try objects are sync")
  val message: Try[String] = for {
    tn <- threadName
    st <- someText
  } yield s"Message: $st was created on t: $tn"
  handleMessage(message)
}
