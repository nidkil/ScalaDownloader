package com.nidkil.downloader.manager

import java.io.File
import com.nidkil.downloader.cleaner.DefaultCleaner
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.io.DownloadProvider
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.splitter.Splitter
import com.nidkil.downloader.utils.Logging
import com.nidkil.downloader.validator.ChecksumValidator
import com.nidkil.downloader.validator.FileSizeValidator
import com.nidkil.downloader.validator.Validator
import org.apache.commons.io.FileUtils
import java.io.PrintWriter
import com.nidkil.downloader.datatypes.RemoteFileInfo
import scala.collection.mutable.LinkedHashSet
import com.nidkil.downloader.datatypes.Chunk

class DefaultDownloadManager(splitter: Splitter, merger: Merger, cleaner: DefaultCleaner) extends DownloadManager(splitter, merger, cleaner) with Logging {

  private var download: Download = null
  private var chunks: LinkedHashSet[Chunk] = null
  private var remoteFileInfo: RemoteFileInfo = null
  
  def writeDebugInfo(): Unit = {
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

  def execute(d: Download) = {
    download = d
    
    val provider = new DownloadProvider()
    
    remoteFileInfo = provider.remoteFileInfo(d.url)
    chunks = splitter.split(remoteFileInfo, d.append, d.workDir)

    writeDebugInfo
    
    for (c <- chunks) provider.download(c)

    merger.merge(d, chunks)

    val result = if (d.checksum == null) {
      val validator = new FileSizeValidator(remoteFileInfo.fileSize)
      validator.validate(merger.tempFile)
    } else {
      val validator = new ChecksumValidator(d.checksum)
      validator.validate(merger.tempFile)
    }
    
    if(!result) {
      val validationType = if(d.checksum == null) "File size" else "Checksum"
      throw new DownloadManagerException(s"$validationType does not match")
    }

    cleaner.clean(d, merger.tempFile)
  }

}