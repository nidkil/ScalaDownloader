package com.nidkil.downloader.datatypes

import java.io.File
import java.net.URL
import com.nidkil.downloader.core.State
import State._

case class Download(id: String, url: URL, destFile: File, workDir: File, append: Boolean = true, state: State = State.NONE)