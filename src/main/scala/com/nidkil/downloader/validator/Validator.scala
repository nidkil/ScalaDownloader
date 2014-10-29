package com.nidkil.downloader.validator

import java.io.File

/** Trait a validator must implement **/
trait Validator {
  type In
  val check: In
  def validate(f: File): Boolean
}