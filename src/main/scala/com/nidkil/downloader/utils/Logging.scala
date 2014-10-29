package com.nidkil.downloader.utils

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

trait Logging {

  val logger = Logger(LoggerFactory.getLogger(getClass.getName))
  
}