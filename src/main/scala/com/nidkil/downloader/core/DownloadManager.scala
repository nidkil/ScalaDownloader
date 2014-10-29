package com.nidkil.downloader.core

import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.splitter.Splitter
import com.nidkil.downloader.validator.Validator

abstract case class DownloadManager(splitter: Splitter, merger: Merger, validator: Validator) {
  
  def execute(download: Download) = {
//    val chunks = splitter.split(download, workingDir)
//    val driver = ProtocolDriverFactory(download.url)
//    
//    for(chunk <- chunks) driver.download(chunk)
//    
//    merger.merge(download, chunk)
//    validator.validate(download)
  }
  
}