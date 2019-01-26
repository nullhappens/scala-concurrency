package org.learningconcurrency.ch2

import org.learningconcurrency.{log, thread}

object SynchronizedNesting extends App{
  import scala.collection._

  class Account(val name: String, var money: Int)

  private val transfers = mutable.ArrayBuffer[String]()

  def logTransfer(name: String, n: Int) = transfers.synchronized{
    transfers += s"transfer to account '$name' = $n"
  }

  def add(account: Account, n: Int) = account.synchronized{
    account.money += n
    logTransfer(account.name, n)
  }

  val jane = new Account("Jane", 100)
  val john = new Account("John", 200)
  val t1 = thread { add(jane, 5) }
  val t2 = thread { add(john, 50) }
  val t3 = thread { add(jane, 70) }
  t1.join(); t2.join(); t3.join()
  log(s"===transfers===\n$transfers")
}
