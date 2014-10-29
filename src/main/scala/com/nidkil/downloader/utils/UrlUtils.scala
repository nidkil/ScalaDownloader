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