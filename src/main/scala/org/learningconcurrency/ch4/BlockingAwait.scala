package org.learningconcurrency
package ch4

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scala.io.Source

object BlockingAwait extends App {
  val urlSpecSizeFuture = Future {
    val specUrl = "http://www.w3.org/Addressing/URL/url-spec.txt"
    Source.fromURL(specUrl).size
  }
  val urlSpecSize = Await.result(urlSpecSizeFuture, 10.seconds)
  log(s"url spec contains $urlSpecSize characters")
}
