package com.nidkil.downloader.cleaner

import java.io.File
import java.io.IOException

import org.apache.commons.io.FileUtils

import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.utils.Logging

/**
 * The cleaner handles the renaming and moving the merged file to the final
 * destination and deletes the directory with chunks.
 *
 * This is the default cleaner implementation.
 */
class DefaultCleaner extends Cleaner with Logging {

  import Merger._

  def clean(download: Download, tempFile: File) = {
    try {
      if(download.destFile.exists) {
        logger.debug(s"Deleting existing destination file [${download.destFile.getPath}]");
        FileUtils.forceDelete(download.destFile)
      }

      logger.debug(s"Moving merged file [from=${tempFile.getPath}, to=${download.destFile.getPath}]");
      FileUtils.moveFile(tempFile, download.destFile)

      logger.debug(s"Deleting working directory [${download.workDir}]");
      FileUtils.forceDelete(download.workDir)
    } catch {
      case e: IOException => {
        val msg = s"Error moving downloaded file or deleting chunks [${e.getMessage}]"
        throw new CleanerException(msg, e);
      }
    }
  }

}