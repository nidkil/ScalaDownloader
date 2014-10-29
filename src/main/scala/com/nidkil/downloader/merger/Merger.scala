package com.nidkil.downloader.merger

import com.nidkil.downloader.utils.Logging
import com.nidkil.downloader.datatypes.Download
import scala.collection.mutable.LinkedHashSet
import com.nidkil.downloader.datatypes.Chunk

object Merger {
  val MERGED_FILE_EXT = ".merged"
}

trait Merger extends Logging {

  def merge(download: Download, chunks: LinkedHashSet[Chunk]): Boolean
  
}