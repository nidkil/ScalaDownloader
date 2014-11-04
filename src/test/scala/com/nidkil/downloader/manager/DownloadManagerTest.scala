package com.nidkil.downloader.manager

import java.io.File
import java.net.URL

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.Tag

import com.nidkil.downloader.cleaner.DefaultCleaner
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.merger.DefaultMerger
import com.nidkil.downloader.splitter.DefaultSplitter
import com.nidkil.downloader.utils.Checksum
import com.nidkil.downloader.utils.Timer
import com.nidkil.downloader.utils.UrlUtils

class DownloadManagerTest extends FunSpec with Matchers {

  def curDir = new java.io.File(".").getCanonicalPath

  describe("A DownloadManager") {
    it("should download the specified file as chunks, merge the chunks and validate the merged file size", Tag("integration")) {
      val timer = new Timer()
      
      timer.start
      
      val splitter = new DefaultSplitter()
      val merger = new DefaultMerger()
      val cleanup = new DefaultCleaner()
      val manager = new DefaultDownloadManager(splitter, merger, cleanup)
      val url = new URL("http://download.thinkbroadband.com/2MB.zip")
      val id = Checksum.calculate(url.toString)
      val downloadDir = new File(curDir, s"download")
      val workDir = new File(downloadDir, id)
      val destFile = new File(downloadDir, UrlUtils.extractFilename(url))
      val download = new Download(id, url, destFile, workDir)
      
      manager.execute(download)
      
      info("destination file must exist")
      assert(destFile.exists)
      
      info("working directory must not exist")
      assert(!workDir.exists)
      
      destFile.delete
      
      timer.stop
      
      info(s"Exectime: ${timer.execTime()}")
    }
    it("should download the specified file as chunks, merge the chunks and validate the merged file checksum", Tag("integration")) {
      val timer = new Timer()
      
      timer.start
      
      val splitter = new DefaultSplitter()
      val merger = new DefaultMerger()
      val cleanup = new DefaultCleaner()
      val manager = new DefaultDownloadManager(splitter, merger, cleanup)
      val url = new URL("http://download.thinkbroadband.com/2MB.zip")
      val id = Checksum.calculate(url.toString)
      val downloadDir = new File(curDir, s"download")
      val workDir = new File(downloadDir, id)
      val destFile = new File(downloadDir, UrlUtils.extractFilename(url))
      val download = new Download(Checksum.calculate(url.toString), url, destFile, workDir, "528972766cd55c26a570829775afd2a8")
      
      manager.execute(download)
      
      info("destination file must exist")
      assert(destFile.exists)
      
      info("working directory must not exist")
      assert(!workDir.exists)
      
      destFile.delete
      
      timer.stop
      
      info(s"Exectime: ${timer.execTime()}")
    }
    it("should throw an exception if the checksum does not match", Tag("integration")) {
      val timer = new Timer()
      
      timer.start
      
      val splitter = new DefaultSplitter()
      val merger = new DefaultMerger()
      val cleanup = new DefaultCleaner()
      val manager = new DefaultDownloadManager(splitter, merger, cleanup)
      val url = new URL("http://download.thinkbroadband.com/2MB.zip")
      val id = Checksum.calculate(url.toString)
      val downloadDir = new File(curDir, s"download")
      val workDir = new File(downloadDir, id)
      val destFile = new File(downloadDir, UrlUtils.extractFilename(url))
      val download = new Download(Checksum.calculate(url.toString), url, destFile, workDir, "b3215c06647bc550406a9c8ccc378756")
      
      intercept[DownloadManagerException] {
        manager.execute(download)
      }
      
      destFile.delete
      
      timer.stop
      
      info(s"Exectime: ${timer.execTime()}")
    }
  }
}