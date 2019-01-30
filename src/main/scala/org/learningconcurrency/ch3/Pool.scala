package org.learningconcurrency.ch3

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

class Pool[T] {
  val parallelism: Int = Runtime.getRuntime.availableProcessors() * 32

  val buckets = new Array[AtomicReference[(List[T], Long)]](parallelism)
  for (i <- buckets.indices)
    buckets(i) = new AtomicReference[(List[T], Long)]((Nil, 0L))

  def add(x: T): Unit = {
    val i = (Thread.currentThread.getId ^ x.## % buckets.length).toInt

    @tailrec def retry(): Unit = {
      val bucket = buckets(i)
      val v = bucket.get()
      val (lst, stamp) = v
      val nlst = x :: lst
      val nstamp = stamp + 1
      val nv = (nlst, nstamp)
      if (!bucket.compareAndSet(v, nv)) retry()
    }
    retry()
  }

  def remove(): Option[T] = {
    val start = (Thread.currentThread.getId % buckets.length).toInt
    @tailrec def scan(witness: Long): Option[T] = {
      var i = (start + 1) % buckets.length
      var sum = 0L
      while (i != start) {
        val bucket = buckets(i)

        @tailrec def retry(): Option[T] = {
          bucket.get match {
            case (Nil, stamp) =>
              sum += stamp
              None
            case v @ (lst, stamp) =>
              val nv = (lst.tail, stamp + 1)
              if (bucket.compareAndSet(v, nv)) Some(lst.head)
              else retry()
          }
        }
        retry() match {
          case Some(v) => return Some(v)
          case None    =>
        }

        i = (i + 1) % buckets.length
      }
      if (sum == witness) None
      else scan(sum)
    }
    scan(-1L)
  }
}
