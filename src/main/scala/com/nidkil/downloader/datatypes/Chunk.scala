package com.nidkil.downloader.datatypes

import java.io.File
import java.net.URL
import com.nidkil.downloader.core.State
import com.nidkil.downloader.core.State._

case class Chunk(id: Int, url: URL, destFile: File, offset: Long, length: Int, append: Boolean = true, state: State = State.NONE)