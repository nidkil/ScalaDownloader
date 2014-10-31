package com.nidkil.downloader.cleaner

import java.io.File
import java.net.URL
import org.apache.commons.io.FileUtils
import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.Tag
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.utils.Checksum

class DefaultCleanerTest extends FunSpec with Matchers {

  import Merger._
  
  val size1mb = 1024 * 1024 * 1
  val size5mb = size1mb * 5
  
  def curDir = new java.io.File(".").getCanonicalPath

  describe("A DefaultCleaner") {
    it("should move the temporary merge file, rename it to the finale name and delete the directory with chunks", Tag("unit")) {
      val url = new URL("http://www.test.com")
      val tempName = Checksum.calculate(url.toString)
      val destFile = new File(curDir, "/download/test.file")
      val workDir = new File(curDir, s"/download/$tempName")
      val tempFile = new File(workDir, s"/$tempName${Merger.MERGED_FILE_EXT}")
      
      if(destFile.exists) destFile.delete
      FileUtils.forceMkdir(workDir)
      tempFile.createNewFile
      
      info(s"destination file does not exist [destFile=$destFile]")
      assert(destFile.exists == false)
      
      info(s"temporary file exists [tempFile=$tempFile]")
      assert(tempFile.exists)
      
      info(s"temporary directory exists [workDir=$workDir]")
      assert(workDir.exists)
      
      val download = new Download("TEST", url, destFile, workDir)
      val cleaner = new DefaultCleaner()
      cleaner.clean(download, tempFile)
      
      info("destination file exists")
      assert(destFile.exists)
      
      info("temporary file has been deleted")
      assert(tempFile.exists == false)
      
      info("temporary directory has been deleted")
      assert(workDir.exists == false)
      
      destFile.delete
    }
  }
}