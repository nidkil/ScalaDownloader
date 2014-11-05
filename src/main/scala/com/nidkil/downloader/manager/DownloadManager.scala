package com.nidkil.downloader.manager

import com.nidkil.downloader.cleaner.Cleaner
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.splitter.Splitter

abstract class DownloadManager(splitter: Splitter, merger: Merger, cleaner: Cleaner) {

  def execute(d: Download, strategy: (Long) => Int = Splitter.defaultStrategy)

}