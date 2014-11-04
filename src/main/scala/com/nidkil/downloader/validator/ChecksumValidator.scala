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