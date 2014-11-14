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
package com.nidkil.downloader.io

import java.io.IOException
import java.net.URL
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import com.nidkil.downloader.utils.Logging
import org.apache.http.impl.client.HttpClientBuilder
import com.nidkil.downloader.datatypes.Chunk
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import com.nidkil.downloader.manager.State

object HttpProtocolDriver {
  val PROTOCOL = "http"
}

/**
 * Implementation of a basic HTTP protocol driver.
 */
class HttpProtocolDriver extends ProtocolDriver with Logging {

  import HttpProtocolDriver._

  def protocol = PROTOCOL

  protected def client: CloseableHttpClient = {
    if (proxy == null) HttpClientBuilder.create.build
    else HttpClientBuilder.create.setProxy(proxy).build
  }

  protected def response(c: Chunk): CloseableHttpResponse = {
    val httpGet = new HttpGet(c.url.toURI)
    val range = s"bytes=${c.offset}-${c.offset + c.length - 1}"

    logger.debug(s"range $range, chunk=$c")
    httpGet.addHeader("range", range)

    val response = try {
      httpClient.execute(httpGet)
    } catch {
      case e: ClientProtocolException => throw new ProtocolDriverException(s"Protocol error occured initializing the URL [${e.getMessage}]", e)
      case e: IOException => throw new ProtocolDriverException(s"IO error occured loading the URL [${e.getMessage}][info=$toString]", e)
    }

    response
  }

}