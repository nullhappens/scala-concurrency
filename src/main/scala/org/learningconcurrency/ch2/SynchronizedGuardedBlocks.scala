package org.learningconcurrency.ch2

import org.learningconcurrency.{log, thread}

object SynchronizedGuardedBlocks extends App{

  val lock = new AnyRef
  var message: Option[String] = None

  val greeter = thread {
    lock.synchronized{
      while(message.isEmpty) lock.wait()
      log(message.get)
    }
  }

  lock.synchronized{
    message = Some("Hello!")
    lock.notify()
  }

  greeter.join()
}
