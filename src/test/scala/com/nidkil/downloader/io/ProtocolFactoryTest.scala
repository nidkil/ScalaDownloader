package com.nidkil.downloader.io

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.Tag

class ProtocolFactoryTest extends FunSpec with Matchers {

  describe("A ProtocolFactory") {
    it("should throw an exception when the protocol does not exists", Tag("unit")) {
      intercept[ProtocolDriverException] {
        val protocolDriver = ProtocolDriverFactory.loadDriver("webdav")
      }
    }
    it("should load the protocol driver if the protocol exist", Tag("unit")) {
      val protocolDriver = ProtocolDriverFactory.loadDriver("http")
      assert(protocolDriver.protocol == "http")
    }
  }

}