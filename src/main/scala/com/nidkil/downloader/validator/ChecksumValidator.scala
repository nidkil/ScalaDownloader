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

import com.nidkil.downloader.utils.Checksum
import com.nidkil.downloader.utils.Logging

/** 
 * Validates that the checksum of a file matches the specified checksum.
 * 
 * @param check the checksum the specified file must match 
 */
class ChecksumValidator(val check: String) extends Validator with Logging {  
  type In = String

  require(check != null, "Checksum cannot be null")
  require(check != "", "Checksum cannot be empty")

  /**
   * Validates the checksum of the file.
   * 
   * @param f is the file to validate the checksum of
   * @return true if the checksum matches, otherwise false
   */
  def validate(f: File): Boolean = {
    require(f != null, "File cannot be null")
    require(f.exists == true, "File must exist")
    
    logger.debug(s"Validating checksum [file=${f.getAbsolutePath}, checksum=$check]")
    
    if(Checksum.calculate(f) == check) true
    else false
  }
}