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
 * This is the default merger which uses standard Java io to merge the chunks.
 */
class DefaultMerger extends Merger {

  import Merger._

  def merge(download: Download, chunks: LinkedHashSet[Chunk]): Boolean = {
    require(download != null, "Download cannot be null")
    require(chunks != null, "Chunks cannot be null")
    
    val tempFile = new File(download.destFile.getParentFile, download.id + MERGED_FILE_EXT)
    
    logger.debug(s"Merge chunks [chunk cnt=${chunks.size}, destination=${tempFile.getPath}]")

    try {
      if (tempFile.exists) FileUtils.forceDelete(tempFile)
    } catch {
      case e: IOException => {
        val msg = s"An error occurred while deleting temp merge file [temp file=${tempFile.getPath}, message=$e.getMessage}]";
        throw new MergerException(msg, e)
      }
    }

    var in: FileInputStream = null
    var out: FileOutputStream = null

    try {
      out = FileUtils.openOutputStream(tempFile)

      for (chunk <- chunks) {
        logger.debug(s"Merging chunk [$chunk.toString}, actual size=${chunk.destFile.length}]")

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