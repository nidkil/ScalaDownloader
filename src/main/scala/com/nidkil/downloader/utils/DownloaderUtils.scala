package com.nidkil.downloader.utils

import com.nidkil.downloader.datatypes.RemoteFileInfo
import org.apache.commons.io.FileUtils
import java.io.PrintWriter
import com.nidkil.downloader.datatypes.Download
import scala.collection.mutable.LinkedHashSet
import java.io.File
import com.nidkil.downloader.datatypes.Chunk

object DownloaderUtils {

  def writeDebugInfo(download: Download, chunks: LinkedHashSet[Chunk], remoteFileInfo: RemoteFileInfo): Unit = {
    if (!download.workDir.exists) FileUtils.forceMkdir(download.workDir)
    val out = new PrintWriter(new File(download.workDir, "debug.info"), "UTF-8")
    try {
      out.println(download)
      out.println(remoteFileInfo)
      out.println(chunks)
    } finally {
      out.close
    }
  }

}