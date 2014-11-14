/*
 * Copyright 2014 nidkil
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nidkil.downloader.merger

import com.nidkil.downloader.datatypes.Download
import scala.collection.mutable.LinkedHashSet
import com.nidkil.downloader.datatypes.Chunk
import java.io.FileInputStream
import org.apache.commons.io.FileUtils
import java.io.FileOutputStream
import java.io.IOException
import org.apache.commons.io.IOUtils
import java.io.File

/**
 * The merger handles the merging of chunks.
 * 
 * This is the default merger which uses standard io to merge the chunks.
 */
class DefaultMerger extends Merger {

  import Merger._
  
  private var _tempFile: File = null

  def tempFile = _tempFile
  
  def merge(download: Download, chunks: LinkedHashSet[Chunk]): Boolean = {
    require(download != null, "Download cannot be null")
    require(chunks != null, "Chunks cannot be null")
    
    _tempFile = new File(download.workDir, download.id + MERGED_FILE_EXT)
    
    logger.debug(s"Merge chunks [chunk cnt=${chunks.size}, destination=${_tempFile.getPath}]")

    try {
      if (_tempFile.exists) FileUtils.forceDelete(_tempFile)
    } catch {
      case e: IOException => {
        val msg = s"An error occurred while deleting temp merge file [temp file=${_tempFile.getPath}, message=$e.getMessage}]";
        throw new MergerException(msg, e)
      }
    }

    var in: FileInputStream = null
    var out: FileOutputStream = null

    try {
      out = FileUtils.openOutputStream(_tempFile)

      for (chunk <- chunks) {
        logger.debug(s"Merging chunk [$chunk, actual size=${chunk.destFile.length}]")

        in = FileUtils.openInputStream(chunk.destFile)

        IOUtils.copy(in, out)

        in.close()
      }

      out.flush();
    } catch {
      case e: IOException => {
        val msg = s"Error merging chunks [${e.getMessage}]"
        throw new MergerException(msg, e);
      }
    } finally {
      IOUtils.closeQuietly(in)
      IOUtils.closeQuietly(out)
    }

    true
  }

}