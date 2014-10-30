package com.nidkil.downloader.behaviour

import org.scalatest.FunSpec
import org.scalatest.Matchers
import com.nidkil.downloader.datatypes.Chunk
import java.io.File
import java.net.URL
import com.nidkil.downloader.utils.UrlUtils
import org.apache.commons.io.FileUtils
import com.nidkil.downloader.core.State
import org.scalatest.Tag

class ChunkDownloadTest extends FunSpec with Matchers {

  import State._
  
  lazy val workDir = new File(curDir, "download")
  lazy val url = new URL("https://download.thinkbroadband.com/5MB.zip")
  lazy val file = new File(workDir, UrlUtils.extractFilename(url))
  lazy val fileSize = 1024 * 1024 * 1 // 1 MB

  def curDir = new java.io.File(".").getCanonicalPath
  
  describe("A ChunkDownload trait") {
    it("should provide validate functionality on demand (composable behaviour)", Tag("unit")) {
      // Note that the last two parameters of Chunk have default values and
      // therefor do not need to be specified when creating the Chunk
      val chunkDownload = new Chunk(1, url, file, 0, fileSize) with ChunkDownload

      FileUtils.forceMkdir(workDir)
      
      info("returns PENDING if the file does not exist")
      file.delete
      val statePending = chunkDownload.validateState(file, fileSize)
      assert(statePending == State.PENDING)

      info("returns DOWNLOADING if the file exists, but does not have the specified length")
      file.createNewFile
      val stateDownloading = chunkDownload.validateState(file, fileSize)
      assert(stateDownloading == State.DOWNLOADING)

      info("returns DOWNLOADED if the file exists, but does not have the specified length")
      val zeroChunkDownload = new Chunk(1, url, file, -1, 0) with ChunkDownload
      val stateDownloaded = zeroChunkDownload.validateState(file, 0)
      assert(stateDownloaded == State.DOWNLOADED)
      file.delete
    }
    it("should provide convenience methods on demand (composable behaviour)", Tag("unit")) {
      val chunkDownload = new Chunk(1, url, file, 0, fileSize) with ChunkDownload
      
      info("return parent directory")
      assert(chunkDownload.workDir == workDir)      
      
      info("return formatted id")
      assert(chunkDownload.formattedId == "000001")      
    }
    it("should be possible to update the state of a Chunk by making a copy and only updating the state", Tag("unit")) {
      val chunkDownload = new Chunk(1, url, file, 0, fileSize) with ChunkDownload
      val updatedChunk = chunkDownload.copy(state = State.COMPLETED)
      assert(chunkDownload.state != updatedChunk.state)
      assert(updatedChunk.state == State.COMPLETED)
    }
  }
  
}