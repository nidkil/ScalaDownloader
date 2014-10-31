package com.nidkil.downloader.io

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import org.apache.commons.io.IOUtils
import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.RemoteFileInfo
import com.nidkil.downloader.utils.Logging
import org.apache.commons.io.FileUtils

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
  
  def download(c: Chunk): Boolean = {
    require(c.url != null, s"Url cannot be null")
    require(c.offset >= 0, s"Offset cannot be negative [offset=${c.offset}]")
    require(c.length > 0, s"Length must be larger than zero [length=${c.length}]")

    driver = ProtocolDriverFactory.loadDriver(c.url.getProtocol)
    
    val connection = driver.connection(c)

    if (!driver.statusOk) {
      val message = s"An error occured retrieving the connection [code=${driver.statusCode}, reason=${driver.statusReason}]"
      throw new DownloadException(message)
    }

    c.destFile match {
      case f: File if f.exists => c.append match {
        case true => logger.debug(s"Destination file exists, resuming download [append=${c.append}, destination=${c.destFile.getAbsolutePath}]")
        case false => {
          logger.debug(s"Destination file exists, deleting file [append=${c.append}, destination=${c.destFile.getAbsolutePath}]")
          f.delete
        }
      }
      case f: File if !f.exists => {
        logger.debug(s"Destination file does NOT exists, creating file [destination=${c.destFile.getAbsolutePath}]")
        FileUtils.forceMkdir(c.destFile.getParentFile)
        c.destFile.createNewFile
      }
    }

    var in: InputStream = null
    var out: OutputStream = null

    try {
      in = new BufferedInputStream(driver.inputstream)
      out = new FileOutputStream(c.destFile, c.append)

      save(in, out)

      out.flush()

      true
    } catch {
      case e: IOException =>
        throw new DownloadException("IO error occurred, could not download file", e)

        false
    } finally {
      IOUtils.closeQuietly(in)
      IOUtils.closeQuietly(out)
    }
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