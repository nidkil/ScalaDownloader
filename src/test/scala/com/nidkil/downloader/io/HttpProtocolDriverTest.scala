package com.nidkil.downloader.io

import java.io.File
import java.net.URL
import java.util.Date
import org.scalatest.FunSpec
import org.scalatest.Matchers
import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.Download
import org.scalatest.Tag

class HttpProtocolDriverTest extends FunSpec with Matchers {

  describe("A HttpProtocolDriver") {
    it("should throw an exception when the protocol is not HTTP", Tag("unit")) {
      intercept[ProtocolDriverException] {
        val driver = new HttpProtocolDriver()
        val chunk = new Chunk(1, new URL("https://www.nu.nl"), new File("test"), 0, 1000)
        val response = driver.connection(chunk)
      }
    }
    it("should throw an exception if the domain does not exist", Tag("unit")) {
      intercept[ProtocolDriverException] {
        val driver = new HttpProtocolDriver()
        val chunk = new Chunk(1, new URL("http://ww.nu.nl"), new File("test"), 0, 1000)
        val response = driver.connection(chunk)
      }
    }
    it("should give a HTTP status code 404 if the URL does NOT exists", Tag("unit")) {
      val driver = new HttpProtocolDriver()
        val chunk = new Chunk(1, new URL("http://www.nu.nl/blabla"), new File("test"), 0, 1000)
        val response = driver.connection(chunk)
        assert(driver.statusCode == 404)
    }
    it("should give a HTTP status code 206 and statusOk if the URL exists", Tag("unit")) {
      val driver = new HttpProtocolDriver()
      val chunk = new Chunk(1, new URL("http://www.nu.nl"), new File("test"), 0, 1000)
      val response = driver.connection(chunk)
      assert(driver.statusCode == 206 && driver.statusOk)
    }
    it("should return information about the download", Tag("unit")) {
      val driver = new HttpProtocolDriver()
      val url = new URL("http://download.thinkbroadband.com/5MB.zip")
      val rfi = driver.remoteFileInfo(url)
      
      info("remote file size")
      assert(rfi.fileSize > 0)
      
      info("content type")
      assert(rfi.mimeType == "application/zip")
      
      info("range support")
      assert(rfi.acceptRanges)
      
      info("range type")
      assert(rfi.lastModified.isInstanceOf[Date])
    }
  }

}