package org.learningconcurrency
package ch4

import scala.concurrent._
import ExecutionContext.Implicits.global

object FuturesCreate extends App {

  Future {
    log("The future is here")
  }
  log("The future is coming")
  Thread.sleep(1000)
}
