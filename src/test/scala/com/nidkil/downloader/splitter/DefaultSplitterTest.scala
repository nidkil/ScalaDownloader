package com.nidkil.downloader.splitter

import java.io.File
import java.net.URL
import java.util.Date
import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.Tag
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.datatypes.RemoteFileInfo
import com.nidkil.downloader.manager.State
import com.nidkil.downloader.merger.GenerateTestFile
import DefaultSplitter.ratioMaxStrategy
import DefaultSplitter.ratioMinMaxStrategy
import DefaultSplitter.ratioStrategy
import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.merger.Merger

class DefaultSplitterTest extends FunSpec with Matchers {

  import Chunk._
  import DefaultSplitter._
  import Merger._

  val size1mb = 1024 * 1024 * 1
  val url = new URL("http://download.thinkbroadband.com/5MB.zip")
  val destFile = new File("test.dat")
  val workDir = new File(curDir, "download/work")

  def customStrategy(fileSize: Long): Int = ((fileSize - (fileSize % 4)) / 4).toInt
  def customStrategy1mb(fileSize: Long): Int = 1024 * 1024 * 1 // 1 MB

  def curDir = new java.io.File(".").getCanonicalPath

  describe("A DefaultSplitter") {
    it("should return 1 chunk when the default splitter used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 5, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir)
      assert(chunks.size == 1)
    }
    it("should return 5 chunk when the default splitter used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 25, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir)
      assert(chunks.size == 5)
    }
    it("should return 10 chunks when the standard ratio strategy is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 512 * 5, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir, ratioStrategy)
      assert(chunks.size == 10)
    }
    it("should return 40 chunks when the standard ratio with max size strategy is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 400, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir, ratioMaxStrategy)
      assert(chunks.size == 40)
    }
    it("should return 5 chunks when the standard ratio with min max size strategy is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 5, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir, ratioMinMaxStrategy)
      assert(chunks.size == 5)
    }
    it("should return 15 chunks when the standard ratio with min max size strategy is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 150, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir, ratioMinMaxStrategy)
      assert(chunks.size == 15)
    }
    it("should return 4 chunks when a custom ratio splitter is used", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, 1024 * 1024 * 5, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir, customStrategy)
      assert(chunks.size == 4)
    }
    it("should return 4 chunks when a custom ratio splitter is used, last chunk should be smaller than 1 MB", Tag("unit")) {
      val info = new RemoteFileInfo(url, "application/x-compressed", true, (1024 * 1024 * 4) - 512, new Date())
      val splitter = new DefaultSplitter()
      val chunks = splitter.split(info, true, workDir, customStrategy1mb)
      assert(chunks.size == 4)
      assert(chunks.last.length < 1024 * 1024 * 1)
    }
    it("should initialize the state of the chunks", Tag("unit")) {
      val f = new File(workDir, "test.file")
      val generateTestChunks = new GenerateTestFile()
      val download = new Download("TEST", new URL("http://www.test.com"), f, f.getParentFile)
      // Chunk states: 1 = Pending, 2 = Downloaded, 3 = Downloading, 4 = Downloaded, 5 = Error
      val chunks = generateTestChunks.generateChunks(workDir, Seq(size1mb, size1mb, size1mb / 2, size1mb, size1mb * 2))
      chunks.find(_.id == 1).get.destFile.delete
      
      val rfi = new RemoteFileInfo(url, "application/x-compressed", true, size1mb * 5, new Date())
      val splitter = new DefaultSplitter()
      val chunks2 = splitter.split(rfi, true, workDir, customStrategy1mb)

      val states = Seq(State.PENDING, State.DOWNLOADED, State.DOWNLOADING, State.DOWNLOADED, State.ERROR)

      for (chunk <- chunks2; state = states lift (chunk.id - 1)) 
        assert(chunk.state == state.getOrElse(State.NONE))

      // Cleanup
      val listOfFiles = f.getParentFile.listFiles
      val matches = for (f <- listOfFiles if f.getName().endsWith(Splitter.CHUNK_FILE_EXT)) yield f
      for (f <- matches) f.delete
    }
  }

}