package com.nidkil.downloader.core

import java.io.File
import java.net.URL

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.Tag

import com.nidkil.downloader.cleaner.DefaultCleaner
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.manager.DefaultDownloadManager;
import com.nidkil.downloader.merger.DefaultMerger
import com.nidkil.downloader.splitter.Splitter
import com.nidkil.downloader.utils.Checksum
import com.nidkil.downloader.utils.Timer
import com.nidkil.downloader.utils.UrlUtils

class DownloadManagerTest extends FunSpec with Matchers {

  def curDir = new java.io.File(".").getCanonicalPath

  describe("A DownloadManager") {
    it("should download the specified file as chunks, merge the chunks and validate the merged file size", Tag("integration")) {
      val timer = new Timer()
      
      timer.start
      
      val splitter = new Splitter()
      val merger = new DefaultMerger()
      val cleanup = new DefaultCleaner()
      val manager = new DefaultDownloadManager(splitter, merger, cleanup)
      val url = new URL("http://download.thinkbroadband.com/5MB.zip")
      val workDir = new File(curDir, "download")
      val destFile = new File(workDir, UrlUtils.extractFilename(url))
      val download = new Download(Checksum.calculate(url.toString), url, destFile, workDir)
      
      manager.execute(download)
      
      timer.stop
      
      info(s"Exectime: ${timer.execTime()}")
    }
    it("should download the specified file as chunks, merge the chunks and validate the merged file checksum", Tag("integration")) {
      val timer = new Timer()
      
      timer.start
      
      val splitter = new Splitter()
      val merger = new DefaultMerger()
      val cleanup = new DefaultCleaner()
      val manager = new DefaultDownloadManager(splitter, merger, cleanup)
      val url = new URL("http://apache.proserve.nl/tomcat/tomcat-7/v7.0.56/bin/apache-tomcat-7.0.56.zip")
      val workDir = new File(curDir, "download")
      val destFile = new File(workDir, UrlUtils.extractFilename(url))
      val download = new Download(Checksum.calculate(url.toString), url, destFile, workDir, "2bc8949a9c2ac44c5787b9ed4cfd3d0d")
      
      manager.execute(download)
      
      timer.stop
      
      info(s"Exectime: ${timer.execTime()}")
    }
  }
}