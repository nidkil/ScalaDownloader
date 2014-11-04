package com.nidkil.downloader.merger

import java.io.File
import java.util.Random
import org.apache.commons.io.FileUtils
import java.io.FileOutputStream
import org.apache.commons.io.IOUtils
import com.nidkil.downloader.datatypes.Chunk
import scala.collection.mutable.LinkedHashSet
import java.net.URL
import com.nidkil.downloader.splitter.Splitter

class GenerateTestFile {

  import Splitter._

  def generateChunks(f: File, size: Long, numChunks: Int, chunkSize: Int): LinkedHashSet[Chunk] = {
    val chunks = LinkedHashSet[Chunk]()
    val random = new Random(System.currentTimeMillis())
    val data = new Array[Byte](chunkSize)

    for (i <- 1 to numChunks) {
      val chunkFile = new File(f.getParentFile, f.getName + f"-$i%06d$CHUNK_FILE_EXT")
      val startChunk = (i - 1) * chunkSize

      random.nextBytes(data)

      writeFile(chunkFile, data)

      chunks += new Chunk(i, new URL("http://www.test.com"), chunkFile, startChunk, chunkSize)
    }

    chunks
  }

  def generateFile(f: File, size: Int) = {
    val random = new Random(System.currentTimeMillis())
    val data = new Array[Byte](size)
    
    random.nextBytes(data)
    writeFile(f, data)
  }

  private def writeFile(f: File, data: Array[Byte]) = {
    var out: FileOutputStream = null

    try {
      out = FileUtils.openOutputStream(f)
      out.write(data)
      out.flush();
    } finally {
      IOUtils.closeQuietly(out)
    }
  }

}