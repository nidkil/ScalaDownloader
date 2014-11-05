package com.nidkil.downloader.manager

import java.io.File
import java.io.PrintWriter

import scala.collection.mutable.LinkedHashSet

import org.apache.commons.io.FileUtils

import com.nidkil.downloader.cleaner.DefaultCleaner
import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.datatypes.RemoteFileInfo
import com.nidkil.downloader.io.DownloadProvider
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.splitter.Splitter
import com.nidkil.downloader.utils.Logging
import com.nidkil.downloader.validator.ChecksumValidator
import com.nidkil.downloader.validator.FileSizeValidator

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

  def execute(d: Download, strategy: (Long) => Int = Splitter.defaultStrategy) = {
    download = d
    
    val p = new DownloadProvider()
    try {
      remoteFileInfo = p.remoteFileInfo(d.url)
    } finally {
      p.close
    }
      
    chunks = splitter.split(remoteFileInfo, d.append, d.workDir, strategy)

    writeDebugInfo
    
    for (c <- chunks.par) {
      // Need to create separate DownloadProviders for each chunk, otherwise 
      // download threads get mixed up 
      val p = new DownloadProvider()
      try {
        p.download(c)
      } finally {
        p.close
      }
    }

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