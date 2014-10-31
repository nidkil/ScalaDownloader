package com.nidkil.downloader.splitter

import org.scalatest.Matchers
import org.scalatest.FunSpec
import com.nidkil.downloader.datatypes.RemoteFileInfo
import com.nidkil.downloader.datatypes.Download
import java.util.zip.Checksum
import java.net.URL
import java.io.File
import java.util.Date
import com.nidkil.downloader.behaviour.ChunkDownload
import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.core.State
import org.scalatest.Tag

class SplitterTest extends FunSpec with Matchers {

  import Splitter._

  val url = new URL("http://download.thinkbroadband.com/5MB.zip")
  val destFile = new File("test.dat")
  val workDir = new File(curDir, "download/work")

  def customStrategy(fileSize: Long): Int = ((fileSize - (fileSize % 4)) / 4).toInt
  def customStrategy1mb(fileSize: Long): Int = 1024 * 1024 * 1 // 1 MB

  def curDir = new java.io.File(".").getCanonicalPath

  describe("A Splitter") {
    it("should return 1 chunk when the default splitter used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 5, new Date())
      val splitter = new Splitter()
      val chunks = splitter.split(info, true, workDir)
      assert(chunks.size == 1)
    }
    it("should return 5 chunk when the default splitter used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 25, new Date())
      val splitter = new Splitter()
      val chunks = splitter.split(info, true, workDir)
      assert(chunks.size == 5)
      val test = for (c <- chunks) yield 
        (new Chunk(c.id, c.url, c.destFile, c.offset, c.length) with ChunkDownload {
          override val state = State.CANCELLED
        })
      for(t <- test) println(s" - ${t.formattedId}, ${t.workDir} $t")
    }
    it("should return 10 chunks when the standard ratio strategy is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 512 * 5, new Date())
      val splitter = new Splitter()
      val chunks = splitter.split(info, true, workDir, ratioStrategy)
      assert(chunks.size == 10)
    }
    it("should return 20 chunks when the standard ratio with max size strategy is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 400, new Date())
      val splitter = new Splitter()
      val chunks = splitter.split(info, true, workDir, ratioMaxSizeStrategy)
      assert(chunks.size == 20)
    }
    it("should return 4 chunks when a custom ratio splitter is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 5, new Date())
      val splitter = new Splitter()
      val chunks = splitter.split(info, true, workDir, customStrategy)
      assert(chunks.size == 4)
    }
    it("should return 4 chunks when a custom ratio splitter is used, last chunk should be smaller than 1 MB", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, (1024 * 1024 * 4) - 512, new Date())
      val splitter = new Splitter()
      val chunks = splitter.split(info, true, workDir, customStrategy1mb)
      assert(chunks.size == 4)
      assert(chunks.last.length < 1024 * 1024 * 1)
    }
  }

}