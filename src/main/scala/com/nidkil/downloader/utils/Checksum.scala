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

import java.security.MessageDigest
import java.io.FileInputStream
import java.io.InputStream
import java.io.File
import org.apache.commons.io.IOUtils

/**
 * Generates a checksum. This checksum is used to verify the integrity of the 
 * file. 
 * 
 * The following algorithms are supported: MD5, SHA-1 and SHA-256. 
 */
object Checksum {

  // Supported algorithms
  val MD5 = "MD5"
  val SHA1 = "SHA-1"
  val SHA256 = "SHA-256"

  /** Calculate the MD5 checksum of a string. **/
  def calculate(s: String): String = calculate(s, MD5)
  
  /** Calculate the checksum of a string with the specified algorithm. **/
  def calculate(s: String, algorithm: String): String = {
    convertToHex(MessageDigest.getInstance(algorithm).digest(s.getBytes))    
  }
  
  /** Calculate the MD5 checksum of a file. **/
  def calculate(file: File): String = calculate(file, MD5)

  /** Calculate the checksum of a file with the specified algorithm. **/
  def calculate(file: File, algorithm: String): String = {
    convertToHex(calcAsBytes(file, algorithm))
  }

  /** Converting a checksum byte array to a hexadecimal string. **/
  private def convertToHex(checksumAsBytes: Array[Byte]): String = {
    val checksum = new StringBuilder()

    for (byte <- checksumAsBytes) checksum.append(Integer.toString((byte & 0xff) + 0x100, 16).substring(1))

    checksum.toString()
  }

  /** 
   *  Calculate a checksum hash as a byte array of the specified file.
   *  
   *  @param f is the file to calculate the checksum of
   *  @param algorithm is the algorithm to use
   *  @return a byte array with the checksum
   **/
  private def calcAsBytes(f: File, algorithm: String): Array[Byte] = {
    var in: InputStream = null;
    var md: MessageDigest = null;

    try {
      in = new FileInputStream(f)
      val buffer = new Array[Byte](1024)
      var numRead = -1

      md = MessageDigest.getInstance(algorithm)

      // Read the file in parts to ensure we do not get an OutOfMemoryError,
      // the MessageDigest update method is used to create the checksum
      Stream.continually(in.read(buffer)).takeWhile(_ >= 0) foreach { read =>
        md.update(buffer, 0, read);
      }
    } finally {
      IOUtils.closeQuietly(in)
    }

    md.digest()
  }

}