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
package com.nidkil.downloader.splitter

import java.io.File

import scala.collection.mutable.LinkedHashSet

import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.RemoteFileInfo

/**
 * A single splitter is provide with a strategy to split files.
 *
 * The following strategy is provided:
 * - defaultStrategy: creates chunks of 5 MB each.
 */
object Splitter {
  val CHUNK_FILE_EXT = ".chunk"

  def defaultStrategy(fileSize: Long): Int = 1024 * 1024 * 5 // 5 MB
}

trait Splitter {

  import Splitter._
  
  def split(r: RemoteFileInfo, append: Boolean, workDir: File, strategy: (Long) => Int = defaultStrategy): LinkedHashSet[Chunk]

}