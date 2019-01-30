package org.learningconcurrency
package ch3

import java.io.File
import java.util.{Timer, TimerTask}
import java.util.concurrent.LinkedBlockingQueue

import scala.collection.convert.decorateAsScala._
import org.apache.commons.io.FileUtils
import org.apache.commons.io.monitor._

import scala.annotation.tailrec
import scala.collection.concurrent
import scala.concurrent.{Future, Promise}

class FileSystem(val root: String) {

  private val messages = new LinkedBlockingQueue[String]()

  private val timer = new Timer(true)

  val rootDir = new File(root)

  val files: concurrent.Map[String, Entry] =
    new concurrent.TrieMap[String, Entry]()

  for (f <- FileUtils.iterateFiles(rootDir, null, false).asScala)
    files.put(f.getName, new Entry(false))

  @tailrec private def prepareForDelete(entry: Entry): Boolean = {
    val s0 = entry.state.get()
    s0 match {
      case idle: Idle =>
        if (entry.state.compareAndSet(s0, new Deleting)) true
        else prepareForDelete(entry)
      case creating: Creating =>
        log("File currently created, cannot delete."); false
      case copying: Copying =>
        log("File currently copied, cannot delete"); false
      case deleting: Deleting => false
    }
  }

  @tailrec private def releaseCopy(e: Entry): Copying = e.state.get match {
    case c: Copying =>
      val nstate = if (c.n == 1) new Idle else new Copying(c.n - 1)
      if (e.state.compareAndSet(c, nstate)) c
      else releaseCopy(e)
  }

  val logger: Thread = new Thread {
    setDaemon(true)
    override def run(): Unit = while (true) log(messages.take())
  }
  logger.start()

  def logMessage(msg: String): Unit = messages.offer(msg)

  def deleteFile(filename: String): Unit = {
    files.get(filename) match {
      case None => logMessage(s"Path '$filename' does not exist")
      case Some(entry) if entry.isDir =>
        logMessage(s"Path '$filename' is a directory!")
      case Some(entry) =>
        execute {
          if (prepareForDelete(entry))
            if (FileUtils.deleteQuietly(new File(filename)))
              files.remove(filename)
        }
    }
  }

  @tailrec private def acquire(entry: Entry): Boolean = {
    val s0 = entry.state.get()
    s0 match {
      case _: Creating | _: Deleting =>
        logMessage("File inaccessible, cannot copy"); false
      case _: Idle =>
        if (entry.state.compareAndSet(s0, new Copying(1))) true
        else acquire(entry)
      case c: Copying =>
        if (entry.state.compareAndSet(s0, new Copying(c.n + 1))) true
        else acquire(entry)
    }
  }

  @tailrec private def release(entry: Entry): Unit = {
    val s0 = entry.state.get()
    s0 match {
      case _: Idle => true
      case _: Creating =>
        if (!entry.state.compareAndSet(s0, new Idle))
          release(entry)
      case copying: Copying =>
        if (copying.n == 1)
          if (!entry.state.compareAndSet(s0, new Idle))
            release(entry)
          else if (!entry.state.compareAndSet(s0, new Copying(copying.n - 1)))
            release(entry)
    }
  }

  def copyFile(src: String, dest: String): Unit = {
    files.get(src) match {
      case Some(srcEntry) if !srcEntry.isDir =>
        execute {
          if (acquire(srcEntry))
            try {
              val destEntry = new Entry(isDir = false)
              destEntry.state.set(new Creating)
              if (files.putIfAbsent(dest, destEntry).isEmpty)
                try {
                  FileUtils.copyFile(new File(src), new File(dest))
                } finally release(destEntry)
            } finally release(srcEntry)
        }
    }
  }

  def timeout(t: Long): Future[Unit] = {
    val p = Promise[Unit]
    timer.schedule(new TimerTask{
      override def run(): Unit = {
        p.success()
        timer.cancel()
      }
    }, t)
    p.future
  }

  def allFiles: Iterable[String] = for ((name, _) <- files) yield name

  def fileCreated(directory: String): Future[String] = {
    val p = Promise[String]
    val fileMonitor = new FileAlterationMonitor(1000)
    val observer = new FileAlterationObserver(directory)
    val listener = new FileAlterationListenerAdaptor {
      override def onFileCreate(file: File): Unit =
        try p.trySuccess(file.getName)
        finally fileMonitor.stop()
    }
    observer.addListener(listener)
    fileMonitor.addObserver(observer)
    fileMonitor.start()
    p.future
  }

}

object FSMain extends App {
  val fs = new FileSystem(".")
  fs.deleteFile("test.txt")

  fs.copyFile("test2.txt", "test3.txt")

  val rootFiles = fs.allFiles
  log(s"All files in the root dir: ${rootFiles.mkString(", ")}")

  Thread.sleep(400)
}
