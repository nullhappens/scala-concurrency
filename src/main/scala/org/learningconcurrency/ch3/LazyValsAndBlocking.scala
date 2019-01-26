package org.learningconcurrency
package ch3

object LazyValsAndBlocking extends App {
  lazy val x: Int = {
    val t = thread(log(s"Initializing $x"))
    t.join()
    1
  }
  x
}
