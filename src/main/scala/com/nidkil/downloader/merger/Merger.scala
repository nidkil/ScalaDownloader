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

import com.nidkil.downloader.utils.Logging
import com.nidkil.downloader.datatypes.Download
import scala.collection.mutable.LinkedHashSet
import com.nidkil.downloader.datatypes.Chunk
import java.io.File

object Merger {
  val MERGED_FILE_EXT = ".merged"
}

trait Merger extends Logging {

  def tempFile: File
  
  def merge(download: Download, chunks: LinkedHashSet[Chunk]): Boolean
  
}