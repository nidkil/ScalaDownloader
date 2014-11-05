package com.nidkil.downloader.io

import java.io.File
import java.net.URL
import org.scalatest.FunSpec
import org.scalatest.Matchers
import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.utils.Checksum
import com.nidkil.downloader.utils.Timer
import com.nidkil.downloader.utils.UrlUtils
import org.scalatest.Tag
import com.nidkil.downloader.manager.State

class DownloadProviderTest extends FunSpec with Matchers {

  val timer = new Timer()
  lazy val workDir = new File(curDir, "download")

  def curDir = new java.io.File(".").getCanonicalPath

  describe("A DownloadProvider") {
    it("should throw an exception when the protocol is not HTTP", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val provider = new DownloadProvider()
        try {
          val url = new URL("https://download.thinkbroadband.com/5MB.zip")
          val file = new File(workDir, UrlUtils.extractFilename(url))
          val chunk = new Chunk(1, url, file, 0, 0)
          provider.download(chunk)
        } finally {
          provider.close
        }
      }
    }
    it("should throw an exception when the URL does not exist") {
      intercept[IllegalArgumentException] {
        val provider = new DownloadProvider()
        try {
          val url = new URL("http://download.thinkbroadband.com/5MB.zip_does_not_exist")
          val file = new File(workDir, UrlUtils.extractFilename(url))
          val chunk = new Chunk(1, url, file, 0, 0)
          provider.download(chunk)
        } finally {
          provider.close
        }
      }
    }
    it("should throw an exception when the offset is negative or the length is zero") {
      intercept[IllegalArgumentException] {
        val provider = new DownloadProvider()
        try {
          val url = new URL("http://download.thinkbroadband.com/5MB.zip_does_not_exist")
          val file = new File(workDir, UrlUtils.extractFilename(url))
          val chunk = new Chunk(1, url, file, -1, 1000)
          provider.download(chunk)
        } finally {
          provider.close
        }
      }
      intercept[IllegalArgumentException] {
        val provider = new DownloadProvider()
        try {
          val url = new URL("http://download.thinkbroadband.com/5MB.zip_does_not_exist")
          val file = new File(workDir, UrlUtils.extractFilename(url))
          val chunk = new Chunk(1, url, file, 0, 0)
          provider.download(chunk)
        } finally {
          provider.close
        }
      }
    }
    it("should throw an exception if state is unknown") {
      intercept[DownloadException] {
        val provider = new DownloadProvider()
        try {
          val url = new URL("http://download.thinkbroadband.com/5MB.zip_does_not_exist")
          val file = new File(workDir, UrlUtils.extractFilename(url))
          val chunk = new Chunk(1, url, file, 0, 1000)
          provider.download(chunk)
        } finally {
          provider.close
        }
      }
    }
    it("should download the file") {
      timer.start

      val provider = new DownloadProvider()

      try {
        val url = new URL("http://download.thinkbroadband.com/5MB.zip")
        val file = new File(workDir, UrlUtils.extractFilename(url))
        val rfi = provider.remoteFileInfo(url)
        val chunk = new Chunk(1, url, file, 0, rfi.fileSize.toInt, true, State.PENDING)
        
        // Make sure the destination file does not exist
        if(file.exists) file.delete

        provider.download(chunk)

        info("the content length must match the file length")
        assert(file.length == rfi.fileSize)

        info("the MD5 checksum of the downloaded file must match the MD5 checksum on the website")
        assert(Checksum.calculate(file) == "b3215c06647bc550406a9c8ccc378756")

        file.delete
      } finally {
        provider.close
      }

      timer.stop

      info(s"download time=${timer.execTime()}")
    }
  }

}