package com.nidkil.downloader.datatypes

import java.io.File
import java.net.URL

import com.nidkil.downloader.manager.State

import State._

case class Download(id: String, url: URL, destFile: File, workDir: File, checksum: String = null, forceDownload: Boolean = true, resumeDownload: Boolean = true, state: State = State.NONE)