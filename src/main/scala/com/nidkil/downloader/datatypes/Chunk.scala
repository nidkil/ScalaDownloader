package com.nidkil.downloader.datatypes

import java.io.File
import java.net.URL

import com.nidkil.downloader.manager.State

import State._

case class Chunk(id: Int, url: URL, destFile: File, offset: Long, length: Int, append: Boolean = true, state: State = State.NONE)