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