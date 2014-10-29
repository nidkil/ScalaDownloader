package com.nidkil.downloader.merger

import com.nidkil.downloader.utils.Logging
import com.nidkil.downloader.datatypes.Download
import scala.collection.mutable.LinkedHashSet
import com.nidkil.downloader.datatypes.Chunk

object Merger {
  val TEMP_EXT = ".merged"
}

trait Merger extends Logging {

  import Merger._

  def merge(download: Download, chunks: LinkedHashSet[Chunk])
  
}