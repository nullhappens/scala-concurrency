package org.learningconcurrency
package ch4

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source
import scala.util.{Failure, Success, Try}

object FuturesFailure extends App {
  val urlSpec: Future[String] = Future {
    val invalidUrl = "http://www.w3.org/non-existent-url-spec.txt"
    Source.fromURL(invalidUrl).mkString
  }

  urlSpec.failed.foreach(e => log(s"exception occurred - $e"))
  Thread.sleep(1000)
}
