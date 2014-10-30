package com.nidkil.downloader.validator

import org.scalatest.Matchers
import org.scalatest.FunSpec
import java.io.File
import org.scalatest.Tag

class ChecksumValidatorTest extends FunSpec with Matchers {

  def curDir = new java.io.File(".").getCanonicalPath
  def testFile = new File(curDir, "LICENSE")

  describe("A ChecksumValidator") {
    it("should throw an IllegalArgumentException if the checksum is null", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val validator = new ChecksumValidator(null)
      }
    }
    it("should throw an IllegalArgumentException if the checksum is empty", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val validator = new ChecksumValidator("")
      }
    }
    it("should throw an IllegalArgumentException if the file is null", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val validator = new ChecksumValidator("9a2abe288cd6a0e2ad52643134565f4d")
        validator.validate(null)
      }
    }
    it("should throw an IllegalArgumentException if the file does not exist", Tag("unit")) {
      intercept[IllegalArgumentException] {
        val validator = new ChecksumValidator("9a2abe288cd6a0e2ad52643134565f4d")
        validator.validate(new File("does_no_exist"))
      }
    }
    it("should return true if the checksum matches", Tag("unit")) {
      // Checksum calculated with Microsoft File Checksum Integrity Verifier tool (fciv)
      val validator = new ChecksumValidator("9a2abe288cd6a0e2ad52643134565f4d")
      assert(validator.validate(testFile))
    }
    it("should return false if the checksum does not match", Tag("unit")) {
      val validator = new ChecksumValidator("1a2abe288cd6a0e2ad52643134565f4z")
      assert(validator.validate(testFile) == false)
    }
  }
  
}