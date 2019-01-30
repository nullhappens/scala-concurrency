package org.learningconcurrency
package ch4

import java.io.File

import org.apache.commons.io.FileUtils
import scala.collection.convert.decorateAsScala._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object FuturesClumsyCallback extends App {

  def blacklistFile(name: String): Future[List[String]] = Future {
    val lines = Source.fromFile(name).getLines()
    lines.filter(x => x.startsWith("#") && !x.isEmpty).toList
  }

  def findFiles(patterns: List[String]): List[String] = {
    val root = new File(".")
    for {
      f <- FileUtils.iterateFiles(root, null, true).asScala.toList
      pat <- patterns
      abspat = root.getCanonicalPath + File.separator + pat
      if f.getCanonicalPath.contains(abspat)
    } yield f.getCanonicalPath
  }

  def blacklisted(name: String): Future[List[String]] =
    blacklistFile(name).map(findFiles)

  val buildFile: Future[Iterator[String]] = Future {
    Source.fromFile("build.sbt").getLines()
  }
  val longest = for (ls <- buildFile) yield ls.maxBy(_.length)
  longest foreach(l => log(s"longest line: $l"))

  blacklistFile(".gitignore").foreach { lines =>
    val files = findFiles(lines)
    log(s"matches: ${files.mkString("\n")}")
  }
  Thread.sleep(1000)

}
