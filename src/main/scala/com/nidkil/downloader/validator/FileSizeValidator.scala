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
package com.nidkil.downloader.validator

import java.io.File

import com.nidkil.downloader.utils.Logging

/** 
 * Validates that the size of a file matches the specified file size.
 * 
 * @param check the file size the specified file must match 
 */
class FileSizeValidator(val check: Long) extends Validator with Logging {
  type In = Long
  
  require(check >= 0, "File size cannot be negative")

  /**
   * Validates the file size of the file.
   * 
   * @param f is the file to validate the size of
   * @return true if the size matches, otherwise false
   */
  def validate(f: File): Boolean = {
    require(f != null, "File cannot be null")
    require(f.exists == true, s"File must exist [${f.getAbsolutePath}]")
    
    logger.debug(s"Validating file size [file=${f.getAbsolutePath}, size=$check}]")

    if(f.length == check) true
    else false
  }
}