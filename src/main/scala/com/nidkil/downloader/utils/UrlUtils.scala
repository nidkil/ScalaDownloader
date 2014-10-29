package com.nidkil.downloader.utils

import java.net.URL

object UrlUtils {

  def extractFilename(url: URL): String = {
    val slashIndex = url.toString.lastIndexOf('/')
    if(slashIndex == -1) ""
    else url.toString.substring(slashIndex + 1)
  }
  
}