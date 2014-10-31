package com.nidkil.downloader.cleaner

import java.io.File

import com.nidkil.downloader.datatypes.Download
import com.nidkil.downloader.utils.Logging

trait Cleaner extends Logging {

  def clean(download: Download, tempFile: File)

}