package org.learningconcurrency
package ch4

import scalaz.concurrent._

object Scalaz extends App {
  val tombola = Future {
    scala.util.Random.shuffle((0 until 10000).toVector)
  }

  tombola.runAsync{ numbers => log(s"And the winner is ${numbers.head}") }
  tombola.runAsync{ numbers => log(s"... wait no, the winner is ${numbers.head}") }
  Thread.sleep(1000)

  val tombolaAsScalaFutureLike = Future {
    scala.util.Random.shuffle((0 until 10000).toVector)
  }.start
}
