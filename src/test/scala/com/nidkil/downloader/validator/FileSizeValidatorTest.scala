package com.nidkil.downloader.validator

import org.scalatest.Matchers
import org.scalatest.FunSpec
import java.io.File
import org.scalatest.Tag

class FileSizeValidatorTest extends FunSpec with Matchers {

  def curDir = new java.io.File(".").getCanonicalPath
  def testFile = new File(curDir, "LICENSE")

  describe("A FileSizeValidator") {
    it("should throw an IllegalArgumentException if the file size is negative", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val validator = new FileSizeValidator(-1L)
      }
    }
    it("should throw an IllegalArgumentException if the file is null", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val validator = new FileSizeValidator(0L)
        validator.validate(null)
      }
    }
    it("should throw an IllegalArgumentException if the file does not exist", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val validator = new FileSizeValidator(0L)
        validator.validate(new File("does_no_exist"))
      }
    }
    it("should return true if the file size matches", Tag("unit")) {
      // File size determined using system properties
      val validator = new FileSizeValidator(11527L)
      assert(validator.validate(testFile))
    }
    it("should return false if the file size does not match", Tag("unit")) {
      val validator = new FileSizeValidator(11500L)
      assert(validator.validate(testFile) == false)
    }
  }
  
}