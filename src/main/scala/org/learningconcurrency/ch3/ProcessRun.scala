package org.learningconcurrency
package ch3

import scala.sys.process._

object ProcessRun extends App {
  val command = "ls"
  val exitcode = command.!
  log(s"command exited with status $exitcode")
}
