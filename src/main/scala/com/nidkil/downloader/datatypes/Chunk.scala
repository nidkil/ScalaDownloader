package com.nidkil.downloader.datatypes

import java.io.File
import java.net.URL
import com.nidkil.downloader.core.State._

case class Chunk(id: Int, url: URL, destFile: File, start : Long, length: Int, append: Boolean, state: State)