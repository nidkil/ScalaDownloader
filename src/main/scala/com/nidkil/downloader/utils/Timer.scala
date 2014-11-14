/*
 * Copyright 2014 nidkil
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nidkil.downloader.utils

class Timer {

  private var startTime = 0L
  private var stopTime = 0L

  def start() = startTime = System.currentTimeMillis

  def stop() = stopTime = System.currentTimeMillis

  def reset() = {
    startTime = 0L
    stopTime = 0L
  }

  def execTime(msecs: Boolean = true): String = {
    val diff = stopTime - startTime
    if (msecs) s"$diff msecs"
    else "%s,%s secs".format(diff / 1000, (diff % 1000).toString().padTo(4, "0").mkString)
  }

}