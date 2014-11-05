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

  def generateChunks(f: File, chunkSizes: Seq[Int]): LinkedHashSet[Chunk] = {
    val chunks = LinkedHashSet[Chunk]()
    val random = new Random(System.currentTimeMillis())
    var prevOffset = 0
    
    for (i <- 1 to chunkSizes.size) {
      val chunkFile = new File(f, f"$i%06d$CHUNK_FILE_EXT")
      val chunkSize = chunkSizes(i - 1)
      val offset = prevOffset
      val data = new Array[Byte](chunkSize)
      
      prevOffset = offset + chunkSize

      random.nextBytes(data)

      writeFile(chunkFile, data)

      chunks += new Chunk(i, new URL("http://www.test.com"), chunkFile, offset, chunkSize)
    }

    chunks  
  }
  
  def generateChunks(f: File, numChunks: Int, chunkSize: Int): LinkedHashSet[Chunk] = {
    def gen(cnt: Int, c: Seq[Int]): Seq[Int] = cnt match {
      case _ if cnt == numChunks => c :+ chunkSize
      case _ => gen(cnt + 1, c :+ chunkSize)
    }
    generateChunks(f, gen(1, Seq()))
  }

  def generateFile(f: File, size: Int) = {
    val random = new Random(System.currentTimeMillis())
    val data = new Array[Byte](size)
    
    random.nextBytes(data)
    writeFile(f, data)
  }

  private def writeFile(f: File, data: Array[Byte]) = {
    var out: FileOutputStream = null

    FileUtils.forceMkdir(f.getParentFile)

    try {
      out = FileUtils.openOutputStream(f)
      out.write(data)
      out.flush();
    } finally {
      IOUtils.closeQuietly(out)
    }
  }

}