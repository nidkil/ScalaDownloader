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
package com.nidkil.downloader.utils

import com.nidkil.downloader.datatypes.RemoteFileInfo
import org.apache.commons.io.FileUtils
import java.io.PrintWriter
import com.nidkil.downloader.datatypes.Download
import scala.collection.mutable.LinkedHashSet
import java.io.File
import com.nidkil.downloader.datatypes.Chunk

object DownloaderUtils {

  def writeDebugInfo(download: Download, chunks: LinkedHashSet[Chunk], remoteFileInfo: RemoteFileInfo): Unit = {
    if (!download.workDir.exists) FileUtils.forceMkdir(download.workDir)
    val out = new PrintWriter(new File(download.workDir, "debug.info"), "UTF-8")
    try {
      out.println(download)
      out.println(remoteFileInfo)
      out.println(chunks)
    } finally {
      out.close
    }
  }

}