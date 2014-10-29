package com.nidkil.downloader.splitter

import java.io.File

import scala.annotation.tailrec
import scala.collection.mutable.LinkedHashSet

import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.RemoteFileInfo

/**
 * The default splitter provides a number of strategies to split files.
 *
 * Three strategies are provided:
 * - defaultStrategy: creates chunks of 5 MB each.
 * - ratioStrategy: creates chunks that are 10% of the filesize.
 * - ratioMaxSizeStrategy: creates chunks that are 10% of the filesize, with a
 *   maximum chunk size of 10 MB.
 */
object Splitter {

  private val ratioMaxSize = 1024 * 1024 * 20 // 20 MB

  def defaultStrategy(fileSize: Long): Int = 1024 * 1024 * 5 // 5 MB

  def ratioStrategy(fileSize: Long): Int = ((fileSize - (fileSize % 10)) / 10).toInt

  def ratioMaxSizeStrategy(fileSize: Long): Int = {
    val chunkSize = ((fileSize - (fileSize % 10)) / 10).toInt
    if (chunkSize < ratioMaxSize) chunkSize else ratioMaxSize
  }

}

/**
 * The Splitter class is responsible for generating the chunks that are used 
 * to download a remote file.
 * 
 * The chunk size is determined based on the file size and splitting strategy.
 */
class Splitter {

  import Splitter._

  /**
   * This method handles the actual generation of chunks.
   * 
   * @param r the information about the remote file to be downloaded
   * @param append indicates if the download must be resumed if chunks exist 
   *        that are not complete
   * @param workDir is the working directory the temporary directory for
   *        the downloaded chunks is created
   * @param strategy is a function used to determine the chunk size
   * @return a LinkedHashSet with Chunks
   */
  def split(r: RemoteFileInfo, append: Boolean, workDir: File, strategy: (Long) => Int = defaultStrategy): LinkedHashSet[Chunk] = {
    val chunks = LinkedHashSet[Chunk]()
    val chunkSize = strategy(r.fileSize)
    val numOfChunks = if (r.fileSize % chunkSize > 0) (r.fileSize / chunkSize).toInt + 1 else (r.fileSize / chunkSize).toInt

    def addChunk(i: Int, startChunk: Long, endChunk: Long, length: Int) = {
      val destFile = new File(workDir, f"$i%06d.chunk")
      chunks += new Chunk(i, r.url, destFile, startChunk, length, append)
    }

    @tailrec def createChunk(i: Int, startChunk: Long): LinkedHashSet[Chunk] = i match {
      case _ if i == numOfChunks => {
        val startChunk = (i - 1) * chunkSize
        addChunk(i, startChunk, r.fileSize, (r.fileSize - startChunk).toInt)
      }
      case _ if i < numOfChunks => {
        val startChunk = (i - 1) * chunkSize
        addChunk(i, startChunk, chunkSize + startChunk - 1, chunkSize.toInt)
        createChunk(i + 1, startChunk)
      }
    }
    createChunk(1, 0)
  }

}