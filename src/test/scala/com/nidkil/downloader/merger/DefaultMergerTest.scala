package com.nidkil.downloader.merger

import java.io.File
import java.net.URL

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.Tag

import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.splitter.Splitter

class DefaultMergerTest extends FunSpec with Matchers {

  val size1mb = 1024 * 1024 * 1
  val size5mb = size1mb * 5
  
  def curDir = new java.io.File(".").getCanonicalPath
  
  describe("A DefaultMerger") {
    it("should merge chunks into a single file", Tag("unit")) {
      val f = new File(curDir, "test.file")
      val generateTestChunks = new GenerateTestFile()
      val download = new Download("TEST", new URL("http://www.test.com"), f, f.getParentFile)
      val chunks = generateTestChunks.generateChunks(f, size5mb, 5, size1mb)
      val mergedFile = new File(curDir, download.id + Merger.MERGED_FILE_EXT)
      val merger = new DefaultMerger()
      
      merger.merge(download, chunks)
      
      info(s"Merged file must exist [${mergedFile.getPath}]")
      assert(mergedFile.exists)
      
      info("File size must be 5 MB")
      assert(mergedFile.length == size5mb)

      // Cleanup
      val listOfFiles = f.getParentFile.listFiles
      val matches = for(f <- listOfFiles if f.getName().endsWith(Splitter.CHUNK_FILE_EXT)) yield f
      for(f <- matches) f.delete
      mergedFile.delete
    }
  }

}