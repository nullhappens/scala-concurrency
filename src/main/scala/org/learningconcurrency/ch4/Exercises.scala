package org.learningconcurrency
package ch4

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits._

object Ex1 extends App{

}

object Ex2 extends App {
  class IVar[T] {
    def apply(): T = ???
    def :=(x: T): Unit = ???
  }
}

object Ex3 extends App {
  implicit class FutureOps[T](val t: Future[T]) {
    def exists(f: T => Boolean): Future[Boolean] =
      t.flatMap(v => Future(f(v)))
  }

  val f1 = Future[Int](2 )
  val f2 = Future[Int](3)
  def isOdd: Int => Boolean = s => s % 2 == 0

  f1.exists(isOdd).foreach{ v => log(s"it does exist with value $v") }
  f2.exists(isOdd).foreach( v => log(s"should be false = $v"))
  Thread.sleep(200)
}
