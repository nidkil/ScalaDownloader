package com.nidkil.downloader.datatypes

import java.io.File
import java.net.URL
import com.nidkil.downloader.DownloaderException
import com.nidkil.downloader.manager.State
import State._
import scala.collection.mutable.LinkedHashSet

case class Chunk(id: Int, url: URL, destFile: File, offset: Long, length: Int, append: Boolean = true, state: State = State.NONE)

/**
 * Convience object that provides a number of convinience methods to change state 
 * of a chunk and update chunks in a Chunk lists.
 */
object Chunk {

  import State._

  /** Create a chunk and initialize the state. **/
  def createChunk(id: Int, url: URL, destFile: File, offset: Long, length: Int, append: Boolean = true, state: State = State.NONE): Chunk = updateState(new Chunk(id, url, destFile, offset, length, append, state))

  /** 
   * Update a chunk in a chunk list. 
   *
   * The old chunk is removed and an updated version of the chunk is added. If 
   * no new chunk is specified the specified chunk is removed.     
   */
  def updateChunk(chunks: LinkedHashSet[Chunk], oldChunk: Chunk, newChunk: Chunk = null): Unit = {
    if(newChunk != null) chunks.add(newChunk)
    chunks.remove(oldChunk)
  }

  /**
   * Iterates through a list of chunks, updates the state of the chunks and
   * returns a new chunk list. 
   */
  def updateStates(chunks: LinkedHashSet[Chunk]): LinkedHashSet[Chunk] = {
    val newList = new LinkedHashSet[Chunk]()
    for (c <- chunks) newList.add(updateState(c))
    newList
  }

  /** Updates the state of the specified chunk using validateState. */
  def updateState(c: Chunk): Chunk = updateState(c, validateState(c))

  /** Updates the state of the specified chunk with the specifed state. */
  def updateState(c: Chunk, s: State): Chunk = {
    s match {
      case State.DOWNLOADING => c.copy(offset = c.offset + c.destFile.length, state = s)
      case State.DOWNLOADED | State.ERROR | State.PENDING => c.copy(state = s)
      case _ => throw new DownloaderException(s"Unknown state [$c]")
    }
  }

  /** Determines the state of a chunk by checking the destination file. */
  def validateState(c: Chunk): State = {
    c.destFile.exists match {
      case true if c.length == c.destFile.length => State.DOWNLOADED
      case true if c.destFile.length < c.length => State.DOWNLOADING
      case true if c.destFile.length > c.length => State.ERROR
      case false => State.PENDING
    }
  }

}