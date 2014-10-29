package com.nidkil.downloader.utils

import java.security.MessageDigest
import java.io.FileInputStream
import java.io.InputStream
import java.io.File

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
    calcAsString(MessageDigest.getInstance(algorithm).digest(s.getBytes))    
  }
  
  /** Calculate the MD5 checksum of a file. **/
  def calculate(file: File): String = calculate(file, MD5)

  /** Calculate the checksum of a file with the specified algorithm. **/
  def calculate(file: File, algorithm: String): String = {
    calcAsString(calcAsBytes(file, algorithm))
  }

  /** Converting a checksum byte array to a hexadecimal string. **/
  private def calcAsString(checksumAsBytes: Array[Byte]): String = {
    val checksum = new StringBuilder()

    for (byte <- checksumAsBytes) checksum.append(Integer.toString((byte & 0xff) + 0x100, 16).substring(1))

    checksum.toString()
  }

  /** 
   *  Calculate a checksum byte array of the specified file.
   *  
   *  @param f is the file to calculate the checksum of
   *  @param algorithm is the algorithm to use
   *  @return a byte array with the checksum
   **/
  private def calcAsBytes(f: File, algorithm: String): Array[Byte] = {
    var in: InputStream = null;
    var complete: MessageDigest = null;

    try {
      in = new FileInputStream(f)
      val buffer = new Array[Byte](1024)
      var numRead = -1

      complete = MessageDigest.getInstance(algorithm)

      Stream.continually(in.read(buffer)).takeWhile(_ >= 0) foreach { read =>
        complete.update(buffer, 0, read);
      }
    } finally {
      if (in != null) in.close
    }

    complete.digest()
  }

}