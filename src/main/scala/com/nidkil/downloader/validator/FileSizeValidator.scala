package com.nidkil.downloader.validator

import java.io.File

/** 
 * Validates that the size of a file matches the specified file size.
 * 
 * @param check the file size the specified file must match 
 */
class FileSizeValidator(val check: Long) extends Validator {
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
    require(f.exists == true, "File must exist")
    
    if(f.length == check) true
    else false
  }
}