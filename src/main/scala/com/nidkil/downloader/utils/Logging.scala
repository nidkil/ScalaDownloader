package com.nidkil.downloader.utils

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

/** Convenience trait to add a logger to a class. **/
trait Logging {
  val logger = Logger(LoggerFactory.getLogger(getClass.getName))
}