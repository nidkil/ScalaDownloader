package com.nidkil.downloader.core

object State extends Enumeration {
  type State = Value
  val NONE, RESUME, PENDING, DOWNLOADING, DOWNLOADED, MERGING, MERGED, COMPLETED, FAILED, CANCELLED, INTERRUPTED = Value
}