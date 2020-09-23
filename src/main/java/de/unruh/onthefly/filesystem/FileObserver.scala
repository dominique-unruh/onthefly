package de.unruh.onthefly.filesystem

import java.nio.file.StandardWatchEventKinds._
import java.nio.file.{FileSystems, Files, Path, Paths, WatchKey}

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.control.Breaks._

sealed abstract class ObservedFileOrDir {
  val path : Path
  protected var _lastChanged: Long = -1
  /** The (recursive) content has not changed since lastChanged.
   * Can be -1 to indicate that the file is currently invalid.
   * Guarantee: if lastChanged is the same (and not -1) before and after a group of reads, then the group of reads is consistent
   * And as long as lastChanged still returns that value, the read content is up-to-date
   */
  def lastChanged : Long = _lastChanged
  def dump(indent: String = "", parent:Path=null): Unit =
    println(s"${indent}${if (parent==null) path; else parent.relativize(path)} (${lastChanged})")
//  private[filesystem] def changed(moment: Long): Unit
  protected[filesystem] def dispose() : Unit
}

final class ObservedFile private[filesystem](val path: Path, moment: Long) extends ObservedFileOrDir {
  changed(moment)
  override protected[filesystem] def dispose(): Unit = {}

  private[filesystem] def changed(moment: Long): Unit =
    _lastChanged = moment
}

final class ObservedDirectory private[filesystem](val path: Path, parent: ObservedDirectory, moment: Long) extends ObservedFileOrDir {
  private val files = mutable.HashMap[Path, ObservedFileOrDir]()
  private var disposed = false
  scan(moment)
  changed(moment)
  private val key = path.register(FileObserver.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW)
  FileObserver.register(key, this)

  @scala.annotation.tailrec
  private def changed(moment: Long): Unit = {
    if (moment != lastChanged) {
      _lastChanged = moment
      if (parent!=null)
        parent.changed(moment)
    }
  }

  private def addFile(filePath: Path, moment: Long): Unit = {
    if (disposed) return
    val fullPath = path.resolve(filePath)
    val file = if (Files.isDirectory(fullPath)) {
      println(s"Add dir: ${filePath} ${fullPath}")
      new ObservedDirectory(fullPath, this, moment)
    } else {
      new ObservedFile(fullPath, moment)
    }
    files.update(filePath, file)
  }

  private def scan(moment: Long): Unit = {
    println(s"Scanning in ${path}")
    // TODO: dispose existing stuff in files
    files.clear()
    for (file <- Files.newDirectoryStream(path).asScala;
         relative = path.relativize(file)) {
      println(s"Found: ${file}")
      addFile(relative, moment)
    }
  }

  override def dump(indent: String, parent: Path): Unit = {
    super.dump(indent, parent)
    val indent2 = indent + "  "
    for ((_,f) <- files)
      f.dump(indent2, path)
  }

  private[filesystem] def triggered(moment: Long): Unit = {
    _lastChanged = -1
    if (disposed) return
    var needScan = false
    breakable {
      for (event <- key.pollEvents().asScala)
        event.kind match {
          case ENTRY_CREATE =>
            println(s"Created: ${event.context()}")
            val filePath = event.context.asInstanceOf[Path]
            addFile(filePath, moment)
          case ENTRY_DELETE =>
            println(s"Deleted: ${event.context}")
            files.remove(event.context.asInstanceOf[Path]) match {
              case Some(file) => file.dispose()
              case None =>
                // Can happen if during an OVERFLOW rescan, we already scanned a state of the directory where the file was gone
            }
          case ENTRY_MODIFY =>
            println(s"Modified: ${event.context}")
            files.get(event.context.asInstanceOf[Path]) match {
              case Some(_: ObservedDirectory) =>
                // Can this happen?
              case Some(file: ObservedFile) => file.changed(moment)
              case None =>
                // Can happen if during an OVERFLOW rescan, we already scanned a state of the directory where the file was gone
            }
            println("Modified")
          case OVERFLOW =>
            println("Overflow")
            scan(moment)
            break()
        }
    }
    changed(moment)
  }

  override protected[filesystem] def dispose(): Unit = {
    disposed = true
    key.cancel()
    for ((_,f) <- files)
      f.dispose()
    files.clear
  }
}

object FileObserver {
  private[filesystem] type Moment = Long
  private[filesystem] val watchService = FileSystems.getDefault.newWatchService()
  private[filesystem] var counter : Moment = 0L
  private val keys = mutable.HashMap[WatchKey, ObservedDirectory]()

  private[filesystem] def register(key: WatchKey, dir: ObservedDirectory): Unit = {
    println(s"Registered key ${key}")
    keys.update(key, dir)
  }

  private def trigger(key: WatchKey): Unit = {
    println(s"Triggered key ${key}")
    val dir = keys(key)
    dir.triggered(counter + 1)
    key.reset()
  }

  private def take(): Unit = {
    var key = watchService.take()
    synchronized {
      trigger(key)
      do {
        key = watchService.poll()
        if (key != null)
          trigger(key)
      } while (key != null)
      counter = counter + 1
    }
  }

  def getDirectory(path: Path): ObservedDirectory = {
    synchronized {
      thread
      val dir = new ObservedDirectory(Paths.get("/tmp/test"), null, counter + 1)
      counter = counter + 1
      dir
    }
  }

  private def mainLoop(): Unit = {
    while (true) {
      take()
    }
  }

  private lazy val thread = {
    val thread = new Thread(mainLoop _ : Runnable, "File Observer")
    thread.setDaemon(true)
    thread.start()
    thread
  }

  def main(args: Array[String]): Unit = {
    thread
    val dir = getDirectory(Paths.get("/tmp/test"))
    while (true) {
      Thread.sleep(1000)
      dir.dump()
//      println(s"Current: ${poll()}")
//      Thread.sleep(1000)
//      take()
    }
  }
}
