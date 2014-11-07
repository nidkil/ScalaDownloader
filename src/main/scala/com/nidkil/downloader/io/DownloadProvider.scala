package com.nidkil.downloader.io

import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.RemoteFileInfo
import com.nidkil.downloader.manager.State
import com.nidkil.downloader.utils.Logging

object DownloadProvider {
  val IO_BUFFER_SIZE = 1 * 1024 * 1024 // 1 MB
}

/**
 * Handles the actual downloading of files.
 */
class DownloadProvider extends Logging {

  import DownloadProvider._

  private var driver: ProtocolDriver = null

  def remoteFileInfo(u: URL): RemoteFileInfo = {
    val driver = ProtocolDriverFactory.loadDriver(u.getProtocol)
    driver.remoteFileInfo(u)
  }

  def download(c: Chunk): Unit = {
    require(c.url != null, s"Url cannot be null")
    require(c.offset >= 0, s"Offset cannot be negative [offset=${c.offset}]")
    require(c.length > 0, s"Length must be larger than zero [length=${c.length}]")

    driver = ProtocolDriverFactory.loadDriver(c.url.getProtocol)

    val connection = driver.connection(c)

    if (!driver.statusOk) {
      val message = s"An error occured retrieving the connection [code=${driver.statusCode}, reason=${driver.statusReason}]"
      throw new DownloadException(message)
    }

    if (prestart(c)) {
      var in: InputStream = null
      var out: OutputStream = null

      try {
        in = new BufferedInputStream(driver.inputstream)
        out = new FileOutputStream(c.destFile, c.append)

        save(in, out)

        out.flush()
      } catch {
        case e: IOException =>
          throw new DownloadException("IO error occurred, could not download file", e)
      } finally {
        IOUtils.closeQuietly(in)
        IOUtils.closeQuietly(out)
      }
    }
  }

  def prestart(c: Chunk): Boolean = {
    var continue = true
    c.state match {
      case State.DOWNLOADED | State.DOWNLOADING if c.append == false => {
        logger.debug(s"Destination file exists append is disabled, deleting file [destFile=${c.destFile.getAbsolutePath}, actual=${c.destFile.length}]")
        FileUtils.forceDelete(c.destFile)
      }
      case State.DOWNLOADED => {
        logger.debug(s"Destination file exists and is complete, skipping download [destFile=${c.destFile.getAbsolutePath}, expected=${c.length}, actual=${c.destFile.length}]")
        continue = false
      }
      case State.DOWNLOADING => {
        logger.debug(s"Destination file exists and is not complete, resuming download [destFile=${c.destFile.getAbsolutePath}]")
      }
      case State.ERROR => {
        logger.debug(s"Destination file exists and is larger than expected size, deleting file [destFile=${c.destFile.getAbsolutePath}, expected=${c.length}, actual=${c.destFile.length}]")
        FileUtils.forceDelete(c.destFile)
      }
      case State.PENDING => {
        logger.debug(s"Destination file does NOT exists, creating file [destFile=${c.destFile.getAbsolutePath}]")
        if(!c.destFile.getParentFile.exists) FileUtils.forceMkdir(c.destFile.getParentFile)
        c.destFile.createNewFile
      }
      case _ => throw new DownloadException(s"Unknown chunk state [$c]")
    }
    continue
  }

  def close: Unit = {
    if (driver != null) driver.close
  }

  //TODO calculate download speed, send download progress to stats component
  private def save(in: InputStream, out: OutputStream) {
    val buffer = new Array[Byte](IO_BUFFER_SIZE)
    var totalRead = 0L
    Stream.continually(in.read(buffer)).takeWhile(_ >= 0) foreach { read =>
      totalRead += read
      out.write(buffer, 0, read)
    }
  }

}