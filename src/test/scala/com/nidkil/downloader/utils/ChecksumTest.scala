package com.nidkil.downloader.utils

import java.security.NoSuchAlgorithmException
import java.io.IOException
import org.scalatest.Matchers
import java.io.File
import org.scalatest.FunSpec

class ChecksumTest extends FunSpec with Matchers {

  def curDir = new java.io.File(".").getCanonicalPath
  def testFile = new File(curDir, "LICENSE")

  describe("A MD5Checksum") {
    it("should throw an IOException when the file does not exist") {
      intercept[IOException] {
        Checksum.calculate(new File("does_not_exist"))
      }
    }
    it("should throw an NoSuchAlgorithmException when the algorithm does not exist") {
      intercept[NoSuchAlgorithmException] {
        Checksum.calculate(testFile, "UNKOWN")
      }
    }
    it("should calculate the MD5 checksum if no algorithm is specifed") {
      val checksum = Checksum.calculate(testFile)
      // Checksum calculated with Microsoft File Checksum Integrity Verifier tool (fciv)
      assert(checksum == "9a2abe288cd6a0e2ad52643134565f4d")
    }
    it("should calculate the MD5 checksum if specified") {
      val checksum = Checksum.calculate(testFile, Checksum.MD5)
      // Checksum calculated with Microsoft File Checksum Integrity Verifier tool (fciv)
      assert(checksum == "9a2abe288cd6a0e2ad52643134565f4d")
    }
    it("should calculate the SHA-1 checksum if specified") {
      val checksum = Checksum.calculate(testFile, Checksum.SHA1)
      // Checksum calculated with Microsoft File Checksum Integrity Verifier tool (fciv)
      assert(checksum == "6b8f5769b47147a271f7d5a808b802dcb44fab85")
    }
    it("should calculate the SHA-256 checksum if specified") {
      val checksum = Checksum.calculate(testFile, Checksum.SHA256)
      assert(checksum.length > 0)
    }
  }

}