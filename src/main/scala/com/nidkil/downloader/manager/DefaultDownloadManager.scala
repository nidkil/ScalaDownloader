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
package com.nidkil.downloader.manager

import java.io.File
import java.io.PrintWriter
import scala.collection.mutable.LinkedHashSet
import org.apache.commons.io.FileUtils
import com.nidkil.downloader.cleaner.DefaultCleaner
import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.datatypes.RemoteFileInfo
import com.nidkil.downloader.io.DownloadProvider
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.splitter.Splitter
import com.nidkil.downloader.utils.Logging
import com.nidkil.downloader.validator.ChecksumValidator
import com.nidkil.downloader.validator.FileSizeValidator
import com.nidkil.downloader.utils.DownloaderUtils

class DefaultDownloadManager(splitter: Splitter, merger: Merger, cleaner: DefaultCleaner) extends DownloadManager(splitter, merger, cleaner) with Logging {

  def execute(d: Download, strategy: (Long) => Int = Splitter.defaultStrategy) = {
    val p = new DownloadProvider()
    val remoteFileInfo = try {
      p.remoteFileInfo(d.url)
    } finally {
      p.close
    }
    val chunks = splitter.split(remoteFileInfo, d.resumeDownload, d.workDir, strategy)

    DownloaderUtils.writeDebugInfo(d, chunks, remoteFileInfo)
    
    if(d.destFile.exists && d.forceDownload) FileUtils.forceDelete(d.destFile)
    
    for (c <- chunks.par) {
      // Need to create separate DownloadProviders for each chunk, otherwise 
      // download threads get mixed up 
      val p = new DownloadProvider()
      try {
        p.download(c)
      } finally {
        p.close
      }
    }

    merger.merge(d, chunks)

    val result = if (d.checksum == null) {
      val validator = new FileSizeValidator(remoteFileInfo.fileSize)
      validator.validate(merger.tempFile)
    } else {
      val validator = new ChecksumValidator(d.checksum)
      validator.validate(merger.tempFile)
    }
    
    if(!result) {
      val validationType = if(d.checksum == null) "File size" else "Checksum"
      throw new DownloadManagerException(s"$validationType does not match")
    }

    cleaner.clean(d, merger.tempFile)
  }

}