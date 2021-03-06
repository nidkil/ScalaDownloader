package com.nidkil.downloader.merger

import java.io.File
import java.net.URL
import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.Tag
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.splitter.Splitter
import org.apache.commons.io.FileUtils

class DefaultMergerTest extends FunSpec with Matchers {

  val size1mb = 1024 * 1024 * 1
  val size5mb = size1mb * 5
  
  def curDir = new java.io.File(".").getCanonicalPath
  
  describe("A DefaultMerger") {
    val workDir = new File(curDir, "download/merger-test")
    val destFile = new File(workDir, "merger.test")
    val generateTestChunks = new GenerateTestFile()
    val download = new Download("TEST", new URL("http://www.test.com"), destFile, workDir)

    it("should merge chunks into a single file, all the same size", Tag("unit")) {
      val chunks = generateTestChunks.generateChunks(destFile, 5, size1mb)
      val mergedFile = new File(workDir, download.id + Merger.MERGED_FILE_EXT)
      val merger = new DefaultMerger()
      
      merger.merge(download, chunks)
      
      info(s"Merged file must exist [${mergedFile.getPath}]")
      assert(mergedFile.exists)
      
      info("File size must be 5 MB")
      assert(mergedFile.length == size5mb)

      // Cleanup
      FileUtils.forceDelete(workDir)
    }
    it("should merge chunks into a single file, all different sizes", Tag("unit")) {
      val chunks = generateTestChunks.generateChunks(destFile, Seq(size1mb, size1mb * 2, size1mb * 3, size1mb * 4, size1mb * 5))
      val mergedFile = new File(workDir, download.id + Merger.MERGED_FILE_EXT)
      val merger = new DefaultMerger()
      
      merger.merge(download, chunks)
      
      info(s"Merged file must exist [${mergedFile.getPath}]")
      assert(mergedFile.exists)
      
      info("File size must be 15 MB")
      assert(mergedFile.length == size1mb * 15)

      // Cleanup
      FileUtils.forceDelete(workDir)
    }
  }

}