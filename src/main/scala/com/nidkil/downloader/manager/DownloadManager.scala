package com.nidkil.downloader.manager

import com.nidkil.downloader.cleaner.DefaultCleaner
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.splitter.Splitter

abstract class DownloadManager(splitter: Splitter, merger: Merger, cleaner: DefaultCleaner) {

  def execute(d: Download)

}