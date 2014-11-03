package com.nidkil.downloader.splitter

import java.io.File

import scala.collection.mutable.LinkedHashSet

import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.RemoteFileInfo

/**
 * A single splitter is provide with a strategy to split files.
 *
 * The following strategy is provided:
 * - defaultStrategy: creates chunks of 5 MB each.
 */
object Splitter {
  val CHUNK_FILE_EXT = ".chunk"

  def defaultStrategy(fileSize: Long): Int = 1024 * 1024 * 5 // 5 MB
}

trait Splitter {

  import Splitter._
  
  def split(r: RemoteFileInfo, append: Boolean, workDir: File, strategy: (Long) => Int = defaultStrategy): LinkedHashSet[Chunk]

}