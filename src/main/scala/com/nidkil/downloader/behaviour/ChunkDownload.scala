package com.nidkil.downloader.behaviour

import java.io.File

import com.nidkil.downloader.manager.State

trait ChunkDownload {
  
  import State._
  
  val destFile: File
  val id: Int

  def workDir: File = destFile.getParentFile
  
  def formattedId: String = f"$id%06d"

  def validateState(f: File, length: Long): State = {
    f.exists match {
      case true if f.length == length => State.DOWNLOADED
      case true if f.length < length => State.DOWNLOADING
      case true => State.ERROR
      case false => State.PENDING
    }
  }
  
}