package org.learningconcurrency
package ch4

import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global

object PromisesCreate extends App {
  val p = Promise[String]
  val q = Promise[String]

  p.future.foreach{ x =>
    log(s"p succeeded with '$x'")
  }

  Thread.sleep(1000)
  p.success("assigned")
  q.failure(new Exception("broken promises"))
  q.future.failed.foreach{ t =>
    log(s"q failed with $t")
  }
  Thread.sleep(1000)
}
