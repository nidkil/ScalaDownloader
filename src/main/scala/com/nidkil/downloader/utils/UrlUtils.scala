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

import java.net.URL

object UrlUtils {

  /**
   * Extracts the name of the file from a URL.
   * 
   * @param url to extract the filename from
   * @return the filename
   */
  def extractFilename(url: URL): String = {
    val slashIndex = url.toString.lastIndexOf('/')
    if(slashIndex == -1) ""
    else url.toString.substring(slashIndex + 1)
  }
  
}