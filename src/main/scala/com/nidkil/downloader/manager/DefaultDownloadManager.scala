package com.nidkil.downloader.manager

import java.io.File

import com.nidkil.downloader.cleaner.DefaultCleaner
import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.io.DownloadProvider
import com.nidkil.downloader.merger.Merger
import com.nidkil.downloader.splitter.Splitter
import com.nidkil.downloader.utils.Logging
import com.nidkil.downloader.validator.ChecksumValidator
import com.nidkil.downloader.validator.FileSizeValidator
import com.nidkil.downloader.validator.Validator

class DefaultDownloadManager(splitter: Splitter, merger: Merger, cleaner: DefaultCleaner) extends DownloadManager(splitter, merger, cleaner) with Logging {

  def execute(d: Download) = {
    val provider = new DownloadProvider()
    val rfi = provider.remoteFileInfo(d.url)
    val chunks = splitter.split(rfi, d.append, new File(d.workDir, d.id))

    for (c <- chunks) provider.download(c)

    merger.merge(d, chunks)

    if (d.checksum == null) {
      val validator = new FileSizeValidator(rfi.fileSize)
      validator.validate(merger.tempFile)
    } else {
      val validator = new ChecksumValidator(d.checksum)
      validator.validate(merger.tempFile)
    }

    cleaner.clean(d, merger.tempFile)
  }

}