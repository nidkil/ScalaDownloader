package com.nidkil.downloader.behaviour

import java.io.File
import com.nidkil.downloader.core.State

trait ChunkDownload {
  
  import State._
  
  val destFile: File
  val id: Int

  def workDir: File = destFile.getParentFile
  
  def formattedId: String = f"$id%06d"

  def validateState(f: File, length: Long): State = {
    if (f.exists) {
      if (f.length == length) State.DOWNLOADED
      else State.DOWNLOADING
    } else State.PENDING
  }
  
}