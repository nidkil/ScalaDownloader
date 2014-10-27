package com.nidkil.downloader.datatypes

import java.util.Date
import java.net.URL

case class RemoteFileInfo (url: URL, mimeType: String, acceptRanges: Boolean, fileSize: Long, lastModified: Date)